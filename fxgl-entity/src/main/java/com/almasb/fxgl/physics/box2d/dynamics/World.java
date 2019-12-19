/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.callbacks.*;
import com.almasb.fxgl.physics.box2d.collision.AABB;
import com.almasb.fxgl.physics.box2d.collision.RayCastInput;
import com.almasb.fxgl.physics.box2d.collision.RayCastOutput;
import com.almasb.fxgl.physics.box2d.collision.TimeOfImpact.TOIInput;
import com.almasb.fxgl.physics.box2d.collision.TimeOfImpact.TOIOutput;
import com.almasb.fxgl.physics.box2d.collision.TimeOfImpact.TOIOutputState;
import com.almasb.fxgl.physics.box2d.collision.broadphase.BroadPhase;
import com.almasb.fxgl.physics.box2d.collision.broadphase.DefaultBroadPhaseBuffer;
import com.almasb.fxgl.physics.box2d.collision.broadphase.DynamicTree;
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.common.Sweep;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Contact;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.ContactEdge;
import com.almasb.fxgl.physics.box2d.dynamics.joints.Joint;
import com.almasb.fxgl.physics.box2d.dynamics.joints.JointDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.JointEdge;
import com.almasb.fxgl.physics.box2d.particle.*;
import com.almasb.fxgl.physics.box2d.pooling.DefaultWorldPool;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

/**
 * The world class manages all physics entities, dynamic simulation, and asynchronous queries.
 * The world also contains efficient memory management facilities.
 *
 * @author Daniel Murphy
 */
public final class World {
    private static final int WORLD_POOL_SIZE = 100;
    private static final int WORLD_POOL_CONTAINER_SIZE = 10;

    private final ContactManager contactManager;
    private final ParticleSystem particleSystem;
    private final IWorldPool pool;

    private DestructionListener destructionListener = null;
    private ParticleDestructionListener particleDestructionListener = null;

    private boolean newFixture = false;

    private boolean locked = false;
    private boolean autoClearForces = true;
    private boolean allowSleep = true;

    // these are for debugging the solver
    private boolean warmStarting = true;
    private boolean continuousPhysics = true;

    private boolean subStepping = false;

    private boolean stepComplete = true;

    private Array<Body> bodies = new Array<>(WORLD_POOL_SIZE);

    private Joint m_jointList = null;
    private int jointCount = 0;

    private final Vec2 gravity = new Vec2();

    public World(Vec2 gravity) {
        this.gravity.set(gravity);

        pool = new DefaultWorldPool(WORLD_POOL_SIZE, WORLD_POOL_CONTAINER_SIZE);

        contactManager = new ContactManager(pool, new DefaultBroadPhaseBuffer(new DynamicTree()));
        particleSystem = new ParticleSystem(this);
    }

    /**
     * Create a rigid body given a definition.
     * No reference to the definition is retained.
     * This function is locked during callbacks.
     *
     * @param def body definition
     * @return rigid body
     */
    public Body createBody(BodyDef def) {
        assertNotLocked();

        Body b = new Body(def, this);

        bodies.add(b);

        return b;
    }

    /**
     * Destroy a rigid body.
     * This automatically deletes all associated shapes and joints.
     * This function is locked during callbacks.
     *
     * @param body body to destroy
     */
    public void destroyBody(Body body) {
        assertNotLocked();

        body.destroy();

        bodies.removeValueByIdentity(body);
        // jbox2dTODO djm recycle body
    }

    /**
     * Create a joint to constrain bodies together.
     * No reference to the definition is retained.
     * This may cause the connected bodies to cease colliding.
     * This function is locked during callbacks.
     * Note: creating a joint doesn't wake the bodies.
     *
     * @param def joint definition
     * @return joint
     */
    public Joint createJoint(JointDef def) {
        assertNotLocked();

        Joint j = Joint.create(this, def);

        // Connect to the world list.
        j.m_prev = null;
        j.m_next = m_jointList;
        if (m_jointList != null) {
            m_jointList.m_prev = j;
        }
        m_jointList = j;
        ++jointCount;

        // Connect to the bodies' doubly linked lists.
        j.m_edgeA.joint = j;
        j.m_edgeA.other = j.getBodyB();
        j.m_edgeA.prev = null;
        j.m_edgeA.next = j.getBodyA().m_jointList;
        if (j.getBodyA().m_jointList != null) {
            j.getBodyA().m_jointList.prev = j.m_edgeA;
        }
        j.getBodyA().m_jointList = j.m_edgeA;

        j.m_edgeB.joint = j;
        j.m_edgeB.other = j.getBodyA();
        j.m_edgeB.prev = null;
        j.m_edgeB.next = j.getBodyB().m_jointList;
        if (j.getBodyB().m_jointList != null) {
            j.getBodyB().m_jointList.prev = j.m_edgeB;
        }
        j.getBodyB().m_jointList = j.m_edgeB;

        Body bodyA = def.bodyA;
        Body bodyB = def.bodyB;

        // If the joint prevents collisions, then flag any contacts for filtering.
        if (!def.collideConnected) {
            ContactEdge edge = bodyB.getContactList();
            while (edge != null) {
                if (edge.other == bodyA) {
                    // Flag the contact for filtering at the next time step (where either body is awake).
                    edge.contact.flagForFiltering();
                }

                edge = edge.next;
            }
        }

        return j;
    }

    /**
     * Destroy a joint. This may cause the connected bodies to begin colliding.
     * This function is locked during callbacks.
     *
     * @param j joint
     */
    public void destroyJoint(Joint j) {
        assertNotLocked();

        boolean collideConnected = j.getCollideConnected();

        // Remove from the doubly linked list.
        if (j.m_prev != null) {
            j.m_prev.m_next = j.m_next;
        }

        if (j.m_next != null) {
            j.m_next.m_prev = j.m_prev;
        }

        if (j == m_jointList) {
            m_jointList = j.m_next;
        }

        // Disconnect from island graph.
        Body bodyA = j.getBodyA();
        Body bodyB = j.getBodyB();

        // Wake up connected bodies.
        bodyA.setAwake(true);
        bodyB.setAwake(true);

        // Remove from body 1.
        if (j.m_edgeA.prev != null) {
            j.m_edgeA.prev.next = j.m_edgeA.next;
        }

        if (j.m_edgeA.next != null) {
            j.m_edgeA.next.prev = j.m_edgeA.prev;
        }

        if (j.m_edgeA == bodyA.m_jointList) {
            bodyA.m_jointList = j.m_edgeA.next;
        }

        j.m_edgeA.prev = null;
        j.m_edgeA.next = null;

        // Remove from body 2
        if (j.m_edgeB.prev != null) {
            j.m_edgeB.prev.next = j.m_edgeB.next;
        }

        if (j.m_edgeB.next != null) {
            j.m_edgeB.next.prev = j.m_edgeB.prev;
        }

        if (j.m_edgeB == bodyB.m_jointList) {
            bodyB.m_jointList = j.m_edgeB.next;
        }

        j.m_edgeB.prev = null;
        j.m_edgeB.next = null;

        Joint.destroy(j);

        assert jointCount > 0;
        --jointCount;

        // If the joint prevents collisions, then flag any contacts for filtering.
        if (!collideConnected) {
            ContactEdge edge = bodyB.getContactList();
            while (edge != null) {
                if (edge.other == bodyA) {
                    // Flag the contact for filtering at the next time step (where either body is awake).
                    edge.contact.flagForFiltering();
                }

                edge = edge.next;
            }
        }
    }

    private final TimeStep step = new TimeStep();

    /**
     * This is used to compute the time step ratio to support a variable time step.
     */
    private float dtInverse = 0;

    /**
     * Take a time step.
     * This performs collision detection, integration, and constraint solution.
     *
     * @param dt the amount of time to simulate, this should not vary
     * @param velocityIterations for the velocity constraint solver
     * @param positionIterations for the position constraint solver
     */
    public void step(float dt, int velocityIterations, int positionIterations) {

        // If new fixtures were added, we need to find the new contacts.
        if (newFixture) {
            contactManager.findNewContacts();
            newFixture = false;
        }

        locked = true;

        step.dt = dt;
        step.velocityIterations = velocityIterations;
        step.positionIterations = positionIterations;
        if (dt > 0.0f) {
            step.inv_dt = 1.0f / dt;
        } else {
            step.inv_dt = 0.0f;
        }

        step.dtRatio = dtInverse * dt;
        step.warmStarting = warmStarting;

        // Update contacts. This is where some contacts are destroyed.
        contactManager.collide();

        if (step.dt > 0) {

            if (stepComplete) {
                // Integrate velocities, solve velocity constraints, and integrate positions
                particleSystem.solve(step); // Particle Simulation
                solve(step);
            }

            if (continuousPhysics) {
                // Handle TOI events.
                solveTOI(step);
            }

            dtInverse = step.inv_dt;
        }

        if (isAutoClearForces()) {
            clearForces();
        }

        locked = false;
    }

    private final Island island = new Island();
    private Body[] stack = new Body[10];

    private void solve(TimeStep step) {
        // update previous transforms
        for (Body b : bodies) {
            b.m_xf0.set(b.m_xf);
        }

        // Size the island for the worst case.
        island.init(getBodyCount(), contactManager.contactCount, jointCount, contactManager.getContactListener());

        // Clear all the island flags.
        for (Body b : bodies) {
            b.m_flags &= ~Body.e_islandFlag;
        }
        for (Contact c = contactManager.contactList; c != null; c = c.m_next) {
            c.m_flags &= ~Contact.ISLAND_FLAG;
        }
        for (Joint j = m_jointList; j != null; j = j.m_next) {
            j.m_islandFlag = false;
        }

        // Build and simulate all awake islands.
        int stackSize = getBodyCount();
        if (stack.length < stackSize) {
            stack = new Body[stackSize];
        }

        for (Body seed : bodies) {
            if ((seed.m_flags & Body.e_islandFlag) == Body.e_islandFlag) {
                continue;
            }

            if (!seed.isAwake() || !seed.isActive()) {
                continue;
            }

            // The seed can be dynamic or kinematic.
            if (seed.getType() == BodyType.STATIC) {
                continue;
            }

            // Reset island and stack.
            island.clear();
            int stackCount = 0;
            stack[stackCount++] = seed;
            seed.m_flags |= Body.e_islandFlag;

            // Perform a depth first search (DFS) on the constraint graph.
            while (stackCount > 0) {
                // Grab the next body off the stack and add it to the island.
                Body b = stack[--stackCount];
                assert b.isActive();
                island.add(b);

                // Make sure the body is awake.
                b.setAwake(true);

                // To keep islands as small as possible, we don't
                // propagate islands across static bodies.
                if (b.getType() == BodyType.STATIC) {
                    continue;
                }

                // Search all contacts connected to this body.
                for (ContactEdge ce = b.m_contactList; ce != null; ce = ce.next) {
                    Contact contact = ce.contact;

                    // Has this contact already been added to an island?
                    if ((contact.m_flags & Contact.ISLAND_FLAG) == Contact.ISLAND_FLAG) {
                        continue;
                    }

                    // Is this contact solid and touching?
                    if (!contact.isEnabled() || !contact.isTouching()) {
                        continue;
                    }

                    // Skip sensors.
                    boolean sensorA = contact.m_fixtureA.isSensor();
                    boolean sensorB = contact.m_fixtureB.isSensor();
                    if (sensorA || sensorB) {
                        continue;
                    }

                    island.add(contact);
                    contact.m_flags |= Contact.ISLAND_FLAG;

                    Body other = ce.other;

                    // Was the other body already added to this island?
                    if ((other.m_flags & Body.e_islandFlag) == Body.e_islandFlag) {
                        continue;
                    }

                    assert stackCount < stackSize;
                    stack[stackCount++] = other;
                    other.m_flags |= Body.e_islandFlag;
                }

                // Search all joints connect to this body.
                for (JointEdge je = b.m_jointList; je != null; je = je.next) {
                    if (je.joint.m_islandFlag) {
                        continue;
                    }

                    Body other = je.other;

                    // Don't simulate joints connected to inactive bodies.
                    if (!other.isActive()) {
                        continue;
                    }

                    island.add(je.joint);
                    je.joint.m_islandFlag = true;

                    if ((other.m_flags & Body.e_islandFlag) == Body.e_islandFlag) {
                        continue;
                    }

                    assert stackCount < stackSize;
                    stack[stackCount++] = other;
                    other.m_flags |= Body.e_islandFlag;
                }
            }
            island.solve(step, gravity, allowSleep);

            // Post solve cleanup.
            for (int i = 0; i < island.m_bodyCount; ++i) {
                // Allow static bodies to participate in other islands.
                Body b = island.m_bodies[i];
                if (b.getType() == BodyType.STATIC) {
                    b.m_flags &= ~Body.e_islandFlag;
                }
            }
        }

        // Synchronize fixtures, check for out of range bodies.
        for (Body b : bodies) {
            // If a body was not in an island then it did not move.
            if ((b.m_flags & Body.e_islandFlag) == 0) {
                continue;
            }

            if (b.getType() == BodyType.STATIC) {
                continue;
            }

            // Update fixtures (for broad-phase).
            b.synchronizeFixtures();
        }

        // Look for new contacts.
        contactManager.findNewContacts();
    }

    private final Island toiIsland = new Island();
    private final TOIInput toiInput = new TOIInput();
    private final TOIOutput toiOutput = new TOIOutput();
    private final TimeStep subStep = new TimeStep();
    private final Body[] tempBodies = new Body[2];
    private final Sweep backup1 = new Sweep();
    private final Sweep backup2 = new Sweep();

    private void solveTOI(final TimeStep step) {

        final Island island = toiIsland;
        island.init(2 * JBoxSettings.maxTOIContacts, JBoxSettings.maxTOIContacts, 0, contactManager.getContactListener());

        if (stepComplete) {
            for (Body b : bodies) {
                b.m_flags &= ~Body.e_islandFlag;
                b.m_sweep.alpha0 = 0.0f;
            }

            for (Contact c = contactManager.contactList; c != null; c = c.m_next) {
                // Invalidate TOI
                c.m_flags &= ~(Contact.TOI_FLAG | Contact.ISLAND_FLAG);
                c.m_toiCount = 0;
                c.m_toi = 1.0f;
            }
        }

        // Find TOI events and solve them.
        for (; ; ) {
            // Find the first TOI.
            Contact minContact = null;
            float minAlpha = 1.0f;

            for (Contact c = contactManager.contactList; c != null; c = c.m_next) {
                // Is this contact disabled?
                if (!c.isEnabled()) {
                    continue;
                }

                // Prevent excessive sub-stepping.
                if (c.m_toiCount > JBoxSettings.maxSubSteps) {
                    continue;
                }

                float alpha = 1.0f;
                if ((c.m_flags & Contact.TOI_FLAG) != 0) {
                    // This contact has a valid cached TOI.
                    alpha = c.m_toi;
                } else {
                    Fixture fA = c.getFixtureA();
                    Fixture fB = c.getFixtureB();

                    // Is there a sensor?
                    if (fA.isSensor() || fB.isSensor()) {
                        continue;
                    }

                    Body bA = fA.getBody();
                    Body bB = fB.getBody();

                    BodyType typeA = bA.getType();
                    BodyType typeB = bB.getType();
                    assert typeA == BodyType.DYNAMIC || typeB == BodyType.DYNAMIC;

                    boolean activeA = bA.isAwake() && typeA != BodyType.STATIC;
                    boolean activeB = bB.isAwake() && typeB != BodyType.STATIC;

                    // Is at least one body active (awake and dynamic or kinematic)?
                    if (!activeA && !activeB) {
                        continue;
                    }

                    boolean collideA = bA.isBullet() || typeA != BodyType.DYNAMIC;
                    boolean collideB = bB.isBullet() || typeB != BodyType.DYNAMIC;

                    // Are these two non-bullet dynamic bodies?
                    if (!collideA && !collideB) {
                        continue;
                    }

                    // Compute the TOI for this contact.
                    // Put the sweeps onto the same time interval.
                    float alpha0 = bA.m_sweep.alpha0;

                    if (bA.m_sweep.alpha0 < bB.m_sweep.alpha0) {
                        alpha0 = bB.m_sweep.alpha0;
                        bA.m_sweep.advance(alpha0);
                    } else if (bB.m_sweep.alpha0 < bA.m_sweep.alpha0) {
                        alpha0 = bA.m_sweep.alpha0;
                        bB.m_sweep.advance(alpha0);
                    }

                    assert alpha0 < 1.0f;

                    int indexA = c.getChildIndexA();
                    int indexB = c.getChildIndexB();

                    // Compute the time of impact in interval [0, minTOI]
                    final TOIInput input = toiInput;
                    input.proxyA.set(fA.getShape(), indexA);
                    input.proxyB.set(fB.getShape(), indexB);
                    input.sweepA.set(bA.m_sweep);
                    input.sweepB.set(bB.m_sweep);
                    input.tMax = 1.0f;

                    pool.getTimeOfImpact().timeOfImpact(toiOutput, input);

                    // Beta is the fraction of the remaining portion of the .
                    float beta = toiOutput.t;
                    if (toiOutput.state == TOIOutputState.TOUCHING) {
                        alpha = Math.min(alpha0 + (1.0f - alpha0) * beta, 1.0f);
                    } else {
                        alpha = 1.0f;
                    }

                    c.m_toi = alpha;
                    c.m_flags |= Contact.TOI_FLAG;
                }

                if (alpha < minAlpha) {
                    // This is the minimum TOI found so far.
                    minContact = c;
                    minAlpha = alpha;
                }
            }

            if (minContact == null || 1.0f - 10.0f * JBoxSettings.EPSILON < minAlpha) {
                // No more TOI events. Done!
                stepComplete = true;
                break;
            }

            // Advance the bodies to the TOI.
            Fixture fA = minContact.getFixtureA();
            Fixture fB = minContact.getFixtureB();
            Body bA = fA.getBody();
            Body bB = fB.getBody();

            backup1.set(bA.m_sweep);
            backup2.set(bB.m_sweep);

            bA.advance(minAlpha);
            bB.advance(minAlpha);

            // The TOI contact likely has some new contact points.
            minContact.update(contactManager.getContactListener());
            minContact.m_flags &= ~Contact.TOI_FLAG;
            ++minContact.m_toiCount;

            // Is the contact solid?
            if (!minContact.isEnabled() || !minContact.isTouching()) {
                // Restore the sweeps.
                minContact.setEnabled(false);
                bA.m_sweep.set(backup1);
                bB.m_sweep.set(backup2);
                bA.synchronizeTransform();
                bB.synchronizeTransform();
                continue;
            }

            bA.setAwake(true);
            bB.setAwake(true);

            // Build the island
            island.clear();
            island.add(bA);
            island.add(bB);
            island.add(minContact);

            bA.m_flags |= Body.e_islandFlag;
            bB.m_flags |= Body.e_islandFlag;
            minContact.m_flags |= Contact.ISLAND_FLAG;

            // Get contacts on bodyA and bodyB.
            tempBodies[0] = bA;
            tempBodies[1] = bB;
            for (int i = 0; i < 2; ++i) {
                Body body = tempBodies[i];
                if (body.getType() == BodyType.DYNAMIC) {
                    for (ContactEdge ce = body.m_contactList; ce != null; ce = ce.next) {
                        if (island.m_bodyCount == island.m_bodyCapacity) {
                            break;
                        }

                        if (island.m_contactCount == island.m_contactCapacity) {
                            break;
                        }

                        Contact contact = ce.contact;

                        // Has this contact already been added to the island?
                        if ((contact.m_flags & Contact.ISLAND_FLAG) != 0) {
                            continue;
                        }

                        // Only add static, kinematic, or bullet bodies.
                        Body other = ce.other;
                        if (other.getType() == BodyType.DYNAMIC && !body.isBullet() && !other.isBullet()) {
                            continue;
                        }

                        // Skip sensors.
                        boolean sensorA = contact.m_fixtureA.isSensor();
                        boolean sensorB = contact.m_fixtureB.isSensor();
                        if (sensorA || sensorB) {
                            continue;
                        }

                        // Tentatively advance the body to the TOI.
                        backup1.set(other.m_sweep);
                        if ((other.m_flags & Body.e_islandFlag) == 0) {
                            other.advance(minAlpha);
                        }

                        // Update the contact points
                        contact.update(contactManager.getContactListener());

                        // Was the contact disabled by the user?
                        if (!contact.isEnabled()) {
                            other.m_sweep.set(backup1);
                            other.synchronizeTransform();
                            continue;
                        }

                        // Are there contact points?
                        if (!contact.isTouching()) {
                            other.m_sweep.set(backup1);
                            other.synchronizeTransform();
                            continue;
                        }

                        // Add the contact to the island
                        contact.m_flags |= Contact.ISLAND_FLAG;
                        island.add(contact);

                        // Has the other body already been added to the island?
                        if ((other.m_flags & Body.e_islandFlag) != 0) {
                            continue;
                        }

                        // Add the other body to the island.
                        other.m_flags |= Body.e_islandFlag;

                        if (other.getType() != BodyType.STATIC) {
                            other.setAwake(true);
                        }

                        island.add(other);
                    }
                }
            }

            subStep.dt = (1.0f - minAlpha) * step.dt;
            subStep.inv_dt = 1.0f / subStep.dt;
            subStep.dtRatio = 1.0f;
            subStep.positionIterations = 20;
            subStep.velocityIterations = step.velocityIterations;
            subStep.warmStarting = false;
            island.solveTOI(subStep, bA.m_islandIndex, bB.m_islandIndex);

            // Reset island flags and synchronize broad-phase proxies.
            for (int i = 0; i < island.m_bodyCount; ++i) {
                Body body = island.m_bodies[i];
                body.m_flags &= ~Body.e_islandFlag;

                if (body.getType() != BodyType.DYNAMIC) {
                    continue;
                }

                body.synchronizeFixtures();

                // Invalidate all contact TOIs on this displaced body.
                for (ContactEdge ce = body.m_contactList; ce != null; ce = ce.next) {
                    ce.contact.m_flags &= ~(Contact.TOI_FLAG | Contact.ISLAND_FLAG);
                }
            }

            // Commit fixture proxy movements to the broad-phase so that new contacts are created.
            // Also, some contacts can be destroyed.
            contactManager.findNewContacts();

            if (subStepping) {
                stepComplete = false;
                break;
            }
        }
    }

    private final WorldQueryWrapper wqwrapper = new WorldQueryWrapper();

    /**
     * Query the world for all fixtures and particles that potentially overlap the provided AABB.
     *
     * @param callback a user implemented callback class
     * @param particleCallback callback for particles
     * @param aabb the query box
     */
    public void queryAABB(QueryCallback callback, ParticleQueryCallback particleCallback, AABB aabb) {
        queryAABB(callback, aabb);
        queryAABB(particleCallback, aabb);
    }

    /**
     * Query the world for all fixtures that potentially overlap the provided AABB.
     *
     * @param callback a user implemented callback class
     * @param aabb the query box
     */
    public void queryAABB(QueryCallback callback, AABB aabb) {
        wqwrapper.broadPhase = contactManager.broadPhase;
        wqwrapper.callback = callback;
        contactManager.broadPhase.query(wqwrapper, aabb);
    }

    /**
     * Query the world for all particles that potentially overlap the provided AABB.
     *
     * @param particleCallback callback for particles
     * @param aabb the query box
     */
    public void queryAABB(ParticleQueryCallback particleCallback, AABB aabb) {
        particleSystem.queryAABB(particleCallback, aabb);
    }

    private final WorldRayCastWrapper wrcwrapper = new WorldRayCastWrapper();
    private final RayCastInput input = new RayCastInput();

    /**
     * Ray-cast the world for all fixtures and particles in the path of the ray.
     * Your callback controls whether you get the closest point, any point, or n-points.
     * The ray-cast ignores shapes that contain the starting point.
     *
     * @param callback a user implemented callback class
     * @param particleCallback the particle callback class
     * @param point1 the ray starting point
     * @param point2 the ray ending point
     */
    public void raycast(RayCastCallback callback, ParticleRaycastCallback particleCallback, Vec2 point1, Vec2 point2) {
        raycast(callback, point1, point2);
        raycast(particleCallback, point1, point2);
    }

    /**
     * Ray-cast the world for all fixtures in the path of the ray.
     * Your callback controls whether you get the closest point, any point, or n-points.
     * The ray-cast ignores shapes that contain the starting point.
     *
     * @param callback a user implemented callback class
     * @param point1 the ray starting point
     * @param point2 the ray ending point
     */
    public void raycast(RayCastCallback callback, Vec2 point1, Vec2 point2) {
        wrcwrapper.broadPhase = contactManager.broadPhase;
        wrcwrapper.callback = callback;
        input.maxFraction = 1.0f;
        input.p1.set(point1);
        input.p2.set(point2);
        contactManager.broadPhase.raycast(wrcwrapper, input);
    }

    /**
     * Ray-cast the world for all particles in the path of the ray.
     * Your callback controls whether you get the closest point, any point, or n-points.
     *
     * @param particleCallback the particle callback class
     * @param point1 the ray starting point
     * @param point2 the ray ending point
     */
    public void raycast(ParticleRaycastCallback particleCallback, Vec2 point1, Vec2 point2) {
        particleSystem.raycast(particleCallback, point1, point2);
    }

    /**
     * Call this after you are done with time steps to clear the forces.
     * You normally call this after each call to Step, unless you are performing sub-steps.
     * By default, forces will be automatically cleared, so you don't need to call this function.
     *
     * @see #setAutoClearForces(boolean)
     */
    public void clearForces() {
        for (Body body : bodies) {
            body.m_force.setZero();
            body.setTorque(0.0f);
        }
    }

    /**
     * Create a particle whose properties have been defined. No reference to the definition is
     * retained. A simulation step must occur before it's possible to interact with a newly created
     * particle. For example, DestroyParticleInShape() will not destroy a particle until Step() has
     * been called. This function is locked during callbacks.
     *
     * @return the index of the particle.
     */
    public int createParticle(ParticleDef def) {
        assertNotLocked();

        return particleSystem.createParticle(def);
    }

    /**
     * Destroy a particle. The particle is removed after the next step.
     *
     * @param index particle index
     */
    public void destroyParticle(int index) {
        destroyParticle(index, false);
    }

    /**
     * Destroy a particle. The particle is removed after the next step.
     *
     * @param index of the particle to destroy
     * @param callDestructionListener whether to call the destruction listener just before the particle is destroyed
     */
    public void destroyParticle(int index, boolean callDestructionListener) {
        particleSystem.destroyParticle(index, callDestructionListener);
    }

    /**
     * Destroy particles inside a shape without enabling the destruction callback for destroyed
     * particles. This function is locked during callbacks. For more information see
     * DestroyParticleInShape(Shape&, Transform&,bool).
     * This function is locked during callbacks.
     *
     * @param shape which encloses particles that should be destroyed.
     * @param xf transform applied to the shape.
     * @return Number of particles destroyed.
     */
    public int destroyParticlesInShape(Shape shape, Transform xf) {
        return destroyParticlesInShape(shape, xf, false);
    }

    /**
     * Destroy particles inside a shape. This function is locked during callbacks. In addition, this
     * function immediately destroys particles in the shape in contrast to DestroyParticle() which
     * defers the destruction until the next simulation step. This function is locked during callbacks.
     *
     * @param shape which encloses particles that should be destroyed.
     * @param xf transform applied to the shape.
     * @param callDestructionListener whether to call the world b2DestructionListener for each particle destroyed.
     * @return Number of particles destroyed.
     */
    public int destroyParticlesInShape(Shape shape, Transform xf, boolean callDestructionListener) {
        assertNotLocked();

        return particleSystem.destroyParticlesInShape(shape, xf, callDestructionListener);
    }

    /**
     * Create a particle group whose properties have been defined. No reference to the definition is
     * retained. This function is locked during callbacks.
     *
     * @param def particle group definition
     * @return particle group
     */
    public ParticleGroup createParticleGroup(ParticleGroupDef def) {
        assertNotLocked();

        return particleSystem.createParticleGroup(def);
    }

    /**
     * Join two particle groups. This function is locked during callbacks.
     *
     * @param groupA the first group. Expands to encompass the second group.
     * @param groupB the second group. It is destroyed.
     */
    public void joinParticleGroups(ParticleGroup groupA, ParticleGroup groupB) {
        assertNotLocked();

        particleSystem.joinParticleGroups(groupA, groupB);
    }

    /**
     * Destroy particles in a group. This function is locked during callbacks.
     *
     * @param group the particle group to destroy.
     * @param callDestructionListener Whether to call the world b2DestructionListener for each particle is destroyed.
     */
    public void destroyParticlesInGroup(ParticleGroup group, boolean callDestructionListener) {
        assertNotLocked();

        particleSystem.destroyParticlesInGroup(group, callDestructionListener);
    }

    /**
     * Destroy particles in a group without enabling the destruction callback for destroyed particles.
     * This function is locked during callbacks.
     *
     * @param group the particle group to destroy.
     */
    public void destroyParticlesInGroup(ParticleGroup group) {
        destroyParticlesInGroup(group, false);
    }

    /**
     * Get the world particle group list. With the returned group, use ParticleGroup::GetNext to get
     * the next group in the world list. A NULL group indicates the end of the list.
     *
     * @return the head of the world particle group list.
     */
    public ParticleGroup[] getParticleGroupList() {
        return particleSystem.getParticleGroupList();
    }

    /**
     * @return the number of particle groups
     */
    public int getParticleGroupCount() {
        return particleSystem.getParticleGroupCount();
    }

    /**
     * @return the number of particles
     */
    public int getParticleCount() {
        return particleSystem.getParticleCount();
    }

    /**
     * @return the maximum number of particles
     */
    public int getParticleMaxCount() {
        return particleSystem.getParticleMaxCount();
    }

    /**
     * Set the maximum number of particles.
     *
     * @param count number
     */
    public void setParticleMaxCount(int count) {
        particleSystem.setParticleMaxCount(count);
    }

    /**
     * Change the particle density.
     *
     * @param density particle density
     */
    public void setParticleDensity(float density) {
        particleSystem.setParticleDensity(density);
    }

    /**
     * @return the particle density
     */
    public float getParticleDensity() {
        return particleSystem.getParticleDensity();
    }

    /**
     * Change the particle gravity scale. Adjusts the effect of the global gravity vector on
     * particles. Default value is 1.0f.
     *
     * @param gravityScale gravity scale
     */
    public void setParticleGravityScale(float gravityScale) {
        particleSystem.setParticleGravityScale(gravityScale);
    }

    /**
     * @return the particle gravity scale
     */
    public float getParticleGravityScale() {
        return particleSystem.getParticleGravityScale();
    }

    /**
     * Damping is used to reduce the velocity of particles. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     *
     * @param damping particle damping
     */
    public void setParticleDamping(float damping) {
        particleSystem.setParticleDamping(damping);
    }

    /**
     * @return damping for particles
     */
    public float getParticleDamping() {
        return particleSystem.getParticleDamping();
    }

    /**
     * Change the particle radius. You should set this only once, on world start. If you change the
     * radius during execution, existing particles may explode, shrink, or behave unexpectedly.
     *
     * @param radius particle radius
     */
    public void setParticleRadius(float radius) {
        particleSystem.setParticleRadius(radius);
    }

    /**
     * @return the particle radius
     */
    public float getParticleRadius() {
        return particleSystem.getParticleRadius();
    }

    /**
     * Get the particle data. Returns the pointer to the head of the particle data.
     *
     * @return particle flags buffer
     */
    public int[] getParticleFlagsBuffer() {
        return particleSystem.getParticleFlagsBuffer();
    }

    public Vec2[] getParticlePositionBuffer() {
        return particleSystem.getParticlePositionBuffer();
    }

    public Vec2[] getParticleVelocityBuffer() {
        return particleSystem.getParticleVelocityBuffer();
    }

    public ParticleColor[] getParticleColorBuffer() {
        return particleSystem.getParticleColorBuffer();
    }

    public ParticleGroup[] getParticleGroupBuffer() {
        return particleSystem.getParticleGroupBuffer();
    }

    public Object[] getParticleUserDataBuffer() {
        return particleSystem.getParticleUserDataBuffer();
    }

    /**
     * Set a buffer for particle data.
     *
     * @param buffer is a pointer to a block of memory.
     * @param capacity is the number of values in the block.
     */
    public void setParticleFlagsBuffer(int[] buffer, int capacity) {
        particleSystem.setParticleFlagsBuffer(buffer, capacity);
    }

    public void setParticlePositionBuffer(Vec2[] buffer, int capacity) {
        particleSystem.setParticlePositionBuffer(buffer, capacity);

    }

    public void setParticleVelocityBuffer(Vec2[] buffer, int capacity) {
        particleSystem.setParticleVelocityBuffer(buffer, capacity);

    }

    public void setParticleColorBuffer(ParticleColor[] buffer, int capacity) {
        particleSystem.setParticleColorBuffer(buffer, capacity);

    }

    public void setParticleUserDataBuffer(Object[] buffer, int capacity) {
        particleSystem.setParticleUserDataBuffer(buffer, capacity);
    }

    /**
     * @return contacts between particles
     */
    public ParticleContact[] getParticleContacts() {
        return particleSystem.m_contactBuffer;
    }

    public int getParticleContactCount() {
        return particleSystem.m_contactCount;
    }

    /**
     * @return contacts between particles and bodies
     */
    public ParticleBodyContact[] getParticleBodyContacts() {
        return particleSystem.m_bodyContactBuffer;
    }

    public int getParticleBodyContactCount() {
        return particleSystem.m_bodyContactCount;
    }

    /**
     * @return the kinetic energy that can be lost by damping force
     */
    public float computeParticleCollisionEnergy() {
        return particleSystem.computeParticleCollisionEnergy();
    }

    /**
     * DO NOT MODIFY.
     *
     * @return all world bodies
     */
    public Array<Body> getBodies() {
        return bodies;
    }

    /**
     * Get the world joint list. With the returned joint, use Joint.getNext to get the next joint in
     * the world list. A null joint indicates the end of the list.
     *
     * @return the head of the world joint list.
     */
    public Joint getJointList() {
        return m_jointList;
    }

    /**
     * Get the world contact list. With the returned contact, use Contact.getNext to get the next
     * contact in the world list. A null contact indicates the end of the list.
     * Contacts are created and destroyed in the middle of a time step.
     * Use ContactListener to avoid missing contacts.
     *
     * @return the head of the world contact list.
     */
    public Contact getContactList() {
        return contactManager.contactList;
    }

    public boolean isSleepingAllowed() {
        return allowSleep;
    }

    public void setSleepingAllowed(boolean sleepingAllowed) {
        allowSleep = sleepingAllowed;
    }

    /**
     * Enable/disable warm starting. For testing.
     *
     * @param flag warm starting flag
     */
    public void setWarmStarting(boolean flag) {
        warmStarting = flag;
    }

    public boolean isWarmStarting() {
        return warmStarting;
    }

    /**
     * Enable/disable continuous physics. For testing.
     *
     * @param flag continuous physics flag
     */
    public void setContinuousPhysics(boolean flag) {
        continuousPhysics = flag;
    }

    public boolean isContinuousPhysics() {
        return continuousPhysics;
    }

    /**
     * @return the number of bodies
     */
    public int getBodyCount() {
        return bodies.size();
    }

    /**
     * @return the number of joints
     */
    public int getJointCount() {
        return jointCount;
    }

    /**
     * @return the number of contacts (each may have 0 or more contact points)
     */
    public int getContactCount() {
        return contactManager.contactCount;
    }

    /**
     * Change the global gravity vector.
     *
     * @param gravity gravity vector
     */
    public void setGravity(Vec2 gravity) {
        this.gravity.set(gravity);
    }

    /**
     * @return global gravity vector
     */
    public Vec2 getGravity() {
        return gravity;
    }

    ContactManager getContactManager() {
        return contactManager;
    }

    public IWorldPool getPool() {
        return pool;
    }

    public DestructionListener getDestructionListener() {
        return destructionListener;
    }

    /**
     * Register a destruction listener. The listener is owned by you and must remain in scope.
     *
     * @param listener destruction listener
     */
    public void setDestructionListener(DestructionListener listener) {
        destructionListener = listener;
    }

    public ParticleDestructionListener getParticleDestructionListener() {
        return particleDestructionListener;
    }

    public void setParticleDestructionListener(ParticleDestructionListener listener) {
        particleDestructionListener = listener;
    }

    public boolean isAllowSleep() {
        return allowSleep;
    }

    public void setAllowSleep(boolean flag) {
        if (flag == allowSleep) {
            return;
        }

        allowSleep = flag;
        if (!allowSleep) {
            for (Body b : bodies) {
                b.setAwake(true);
            }
        }
    }

    public void setSubStepping(boolean subStepping) {
        this.subStepping = subStepping;
    }

    public boolean isSubStepping() {
        return subStepping;
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    /**
     * Set flag to control automatic clearing of forces after each time step.
     *
     * @param flag automatically clear forces flag
     */
    public void setAutoClearForces(boolean flag) {
        autoClearForces = flag;
    }

    /**
     * @return the flag that controls automatic clearing of forces after each time step
     */
    public boolean isAutoClearForces() {
        return autoClearForces;
    }

    /**
     * Register a contact filter to provide specific control over collision.
     * Otherwise the default filter is used (_defaultFilter).
     * The listener is owned by you and must remain in scope.
     *
     * @param filter contact filter
     */
    public void setContactFilter(ContactFilter filter) {
        contactManager.setcontactFilter(filter);
    }

    /**
     * Register a contact event listener. The listener is owned by you and must remain in scope.
     *
     * @param listener contact listener
     */
    public void setContactListener(ContactListener listener) {
        contactManager.setContactListener(listener);
    }

    void notifyNewFixture() {
        newFixture = true;
    }

    /**
     * @return is the world locked (in the middle of a time step)
     */
    public boolean isLocked() {
        return locked;
    }

    void assertNotLocked() {
        if (isLocked())
            throw new IllegalStateException("Physics world is locked during time step");
    }

    private static class WorldQueryWrapper implements TreeCallback {
        BroadPhase broadPhase;
        QueryCallback callback;

        @Override
        public boolean treeCallback(int nodeId) {
            Fixture.FixtureProxy proxy = (Fixture.FixtureProxy) broadPhase.getUserData(nodeId);
            return callback.reportFixture(proxy.fixture);
        }
    }

    private static class WorldRayCastWrapper implements TreeRayCastCallback {

        // djm pooling
        private final RayCastOutput output = new RayCastOutput();
        private final Vec2 temp = new Vec2();
        private final Vec2 point = new Vec2();

        BroadPhase broadPhase;
        RayCastCallback callback;

        @Override
        public float raycastCallback(RayCastInput input, int nodeId) {
            Object userData = broadPhase.getUserData(nodeId);
            Fixture.FixtureProxy proxy = (Fixture.FixtureProxy) userData;
            Fixture fixture = proxy.fixture;
            int index = proxy.childIndex;
            boolean hit = fixture.raycast(output, input, index);

            if (hit) {
                float fraction = output.fraction;
                // Vec2 point = (1.0f - fraction) * input.p1 + fraction * input.p2;
                temp.set(input.p2).mulLocal(fraction);
                point.set(input.p1).mulLocal(1 - fraction).addLocal(temp);
                return callback.reportFixture(fixture, point, output.normal, fraction);
            }

            return input.maxFraction;
        }
    }
}