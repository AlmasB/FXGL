/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.physics.box2d.callbacks.ContactListener;
import com.almasb.fxgl.physics.box2d.collision.ContactID;
import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.ManifoldPoint;
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape;
import com.almasb.fxgl.physics.box2d.common.JBoxUtils;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

/**
 * The class manages contact between two shapes.
 * A contact exists for each overlapping AABB in the broad-phase (except if filtered).
 * Therefore a contact object may exist that has no contact points.
 *
 * @author daniel
 */
public abstract class Contact {

    // Flags stored in m_flags
    // Used when crawling contact graph when forming islands.
    public static final int ISLAND_FLAG = 0x0001;
    // Set when the shapes are touching.
    public static final int TOUCHING_FLAG = 0x0002;
    // This contact can be disabled (by user)
    public static final int ENABLED_FLAG = 0x0004;
    // This contact needs filtering because a fixture filter was changed.
    public static final int FILTER_FLAG = 0x0008;
    // This bullet contact had a TOI event
    public static final int BULLET_HIT_FLAG = 0x0010;

    public static final int TOI_FLAG = 0x0020;

    public int m_flags;

    // World pool and list pointers.
    public Contact m_prev;
    public Contact m_next;

    // Nodes for connecting bodies.
    public ContactEdge m_nodeA = new ContactEdge();
    public ContactEdge m_nodeB = new ContactEdge();

    public Fixture m_fixtureA = null;
    public Fixture m_fixtureB = null;

    public int m_indexA;
    public int m_indexB;

    public final Manifold m_manifold = new Manifold();

    public float m_toiCount;
    public float m_toi;

    public float m_friction;
    public float m_restitution;

    public float m_tangentSpeed;

    protected final IWorldPool pool;

    protected Contact(IWorldPool argPool) {
        pool = argPool;
    }

    /** initialization for pooling */
    public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
        m_flags = ENABLED_FLAG;

        m_fixtureA = fA;
        m_fixtureB = fB;

        m_indexA = indexA;
        m_indexB = indexB;

        m_manifold.pointCount = 0;

        m_prev = null;
        m_next = null;

        m_nodeA.contact = null;
        m_nodeA.prev = null;
        m_nodeA.next = null;
        m_nodeA.other = null;

        m_nodeB.contact = null;
        m_nodeB.prev = null;
        m_nodeB.next = null;
        m_nodeB.other = null;

        m_toiCount = 0;
        m_friction = mixFriction(fA.getFriction(), fB.getFriction());
        m_restitution = mixRestitution(fA.getRestitution(), fB.getRestitution());

        m_tangentSpeed = 0;
    }

    /**
     * Get the contact manifold. Do not set the point count to zero. Instead call Disable.
     */
    public Manifold getManifold() {
        return m_manifold;
    }

    /**
     * @return is this contact touching
     */
    public boolean isTouching() {
        return (m_flags & TOUCHING_FLAG) == TOUCHING_FLAG;
    }

    /**
     * Enable/disable this contact. This can be used inside the pre-solve contact listener. The
     * contact is only disabled for the current time step (or sub-step in continuous collisions).
     *
     * @param flag
     */
    public void setEnabled(boolean flag) {
        if (flag) {
            m_flags |= ENABLED_FLAG;
        } else {
            m_flags &= ~ENABLED_FLAG;
        }
    }

    public boolean isEnabled() {
        return (m_flags & ENABLED_FLAG) == ENABLED_FLAG;
    }

    /**
     * @return the next contact in the world's contact list
     */
    public Contact getNext() {
        return m_next;
    }

    /**
     * @return the first fixture in this contact
     */
    public Fixture getFixtureA() {
        return m_fixtureA;
    }

    public int getChildIndexA() {
        return m_indexA;
    }

    /**
     * @return the second fixture in this contact
     */
    public Fixture getFixtureB() {
        return m_fixtureB;
    }

    public int getChildIndexB() {
        return m_indexB;
    }

    public void setFriction(float friction) {
        m_friction = friction;
    }

    public float getFriction() {
        return m_friction;
    }

    public void resetFriction() {
        m_friction = mixFriction(m_fixtureA.getFriction(), m_fixtureB.getFriction());
    }

    public void setRestitution(float restitution) {
        m_restitution = restitution;
    }

    public float getRestitution() {
        return m_restitution;
    }

    public void resetRestitution() {
        m_restitution = mixRestitution(m_fixtureA.getRestitution(), m_fixtureB.getRestitution());
    }

    public void setTangentSpeed(float speed) {
        m_tangentSpeed = speed;
    }

    public float getTangentSpeed() {
        return m_tangentSpeed;
    }

    public abstract void evaluate(Manifold manifold, Transform xfA, Transform xfB);

    /**
     * Flag this contact for filtering. Filtering will occur the next time step.
     */
    public void flagForFiltering() {
        m_flags |= FILTER_FLAG;
    }

    // djm pooling
    private final Manifold oldManifold = new Manifold();

    public void update(ContactListener listener) {
        oldManifold.set(m_manifold);

        // Re-enable this contact.
        m_flags |= ENABLED_FLAG;

        boolean wasTouching = (m_flags & TOUCHING_FLAG) == TOUCHING_FLAG;

        boolean sensorA = m_fixtureA.isSensor();
        boolean sensorB = m_fixtureB.isSensor();
        boolean sensor = sensorA || sensorB;

        Body bodyA = m_fixtureA.getBody();
        Body bodyB = m_fixtureB.getBody();
        Transform xfA = bodyA.getTransform();
        Transform xfB = bodyB.getTransform();

        boolean touching;

        if (sensor) {
            Shape shapeA = m_fixtureA.getShape();
            Shape shapeB = m_fixtureB.getShape();
            touching = pool.getCollision().testOverlap(shapeA, m_indexA, shapeB, m_indexB, xfA, xfB);

            // Sensors don't generate manifolds.
            m_manifold.pointCount = 0;
        } else {
            evaluate(m_manifold, xfA, xfB);
            touching = m_manifold.pointCount > 0;

            // Match old contact ids to new contact ids and copy the
            // stored impulses to warm start the solver.
            for (int i = 0; i < m_manifold.pointCount; ++i) {
                ManifoldPoint mp2 = m_manifold.points[i];
                mp2.normalImpulse = 0.0f;
                mp2.tangentImpulse = 0.0f;
                ContactID id2 = mp2.id;

                for (int j = 0; j < oldManifold.pointCount; ++j) {
                    ManifoldPoint mp1 = oldManifold.points[j];

                    if (mp1.id.isEqual(id2)) {
                        mp2.normalImpulse = mp1.normalImpulse;
                        mp2.tangentImpulse = mp1.tangentImpulse;
                        break;
                    }
                }
            }

            if (touching != wasTouching) {
                bodyA.setAwake(true);
                bodyB.setAwake(true);
            }
        }

        if (touching) {
            m_flags |= TOUCHING_FLAG;
        } else {
            m_flags &= ~TOUCHING_FLAG;
        }

        if (listener == null) {
            return;
        }

        if (!wasTouching && touching) {
            listener.beginContact(this);
        }

        if (wasTouching && !touching) {
            listener.endContact(this);
        }

        if (!sensor && touching) {
            listener.preSolve(this, oldManifold);
        }
    }

    /**
     * Friction mixing law.
     * The idea is to allow either fixture to drive the restitution to zero.
     * For example, anything slides on ice.
     */
    private static float mixFriction(float friction1, float friction2) {
        return JBoxUtils.sqrt(friction1 * friction2);
    }

    /**
     * Restitution mixing law.
     * The idea is allow for anything to bounce off an inelastic surface.
     * For example, a superball bounces on anything.
     */
    private static float mixRestitution(float restitution1, float restitution2) {
        return restitution1 > restitution2 ? restitution1 : restitution2;
    }
}