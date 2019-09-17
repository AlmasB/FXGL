/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.box2d.collision.broadphase.BroadPhase;
import com.almasb.fxgl.physics.box2d.collision.shapes.MassData;
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape;
import com.almasb.fxgl.physics.box2d.common.JBoxUtils;
import com.almasb.fxgl.physics.box2d.common.Rotation;
import com.almasb.fxgl.physics.box2d.common.Sweep;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Contact;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.ContactEdge;
import com.almasb.fxgl.physics.box2d.dynamics.joints.JointEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * A rigid body.
 * These are created via World.createBody.
 *
 * @author Daniel Murphy
 */
public final class Body {
    static final int e_islandFlag = 0x0001;

    private static final int e_awakeFlag = 0x0002;
    private static final int e_autoSleepFlag = 0x0004;
    private static final int e_bulletFlag = 0x0008;
    private static final int e_fixedRotationFlag = 0x0010;
    private static final int e_activeFlag = 0x0020;

    private final World world;
    private BodyType type;

    private List<Fixture> fixtures = new ArrayList<>();

    public JointEdge m_jointList = null;
    public ContactEdge m_contactList = null;

    public int m_flags = 0;

    public int m_islandIndex;

    /**
     * The body origin transform.
     */
    public final Transform m_xf = new Transform();

    /**
     * The previous transform for particle simulation
     */
    public final Transform m_xf0 = new Transform();

    /**
     * The swept motion for CCD
     */
    public final Sweep m_sweep = new Sweep();

    public final Vec2 m_linearVelocity = new Vec2();
    private float m_angularVelocity;

    public final Vec2 m_force = new Vec2();
    private float m_torque = 0;

    public float m_mass, m_invMass;

    // Rotational inertia about the center of mass.
    private float m_I = 0;
    public float m_invI = 0;

    private float linearDamping;
    private float angularDamping;
    private float gravityScale;

    private float sleepTime = 0;

    private Object userData;

    private Entity entity;

    Body(BodyDef bd, World world) {
        checkValid(bd);

        this.world = world;
        userData = bd.getUserData();

        if (bd.isBullet()) {
            m_flags |= e_bulletFlag;
        }
        if (bd.isFixedRotation()) {
            m_flags |= e_fixedRotationFlag;
        }
        if (bd.isAllowSleep()) {
            m_flags |= e_autoSleepFlag;
        }
        if (bd.isAwake()) {
            m_flags |= e_awakeFlag;
        }
        if (bd.isActive()) {
            m_flags |= e_activeFlag;
        }

        m_xf.p.set(bd.getPosition());
        m_xf.q.set(bd.getAngle());

        m_sweep.localCenter.setZero();
        m_sweep.c0.set(m_xf.p);
        m_sweep.c.set(m_xf.p);
        m_sweep.a0 = bd.getAngle();
        m_sweep.a = bd.getAngle();
        m_sweep.alpha0 = 0.0f;

        m_linearVelocity.set(bd.getLinearVelocity());
        m_angularVelocity = bd.getAngularVelocity();

        linearDamping = bd.getLinearDamping();
        angularDamping = bd.getAngularDamping();
        gravityScale = bd.getGravityScale();

        type = bd.getType();

        if (type == BodyType.DYNAMIC) {
            m_mass = 1f;
            m_invMass = 1f;
        } else {
            m_mass = 0f;
            m_invMass = 0f;
        }
    }

    private void checkValid(BodyDef def) {
        if (def.getGravityScale() < 0)
            throw new IllegalArgumentException("Gravity scale is invalid");

        if (def.getAngularDamping() < 0)
            throw new IllegalArgumentException("Angular damping is invalid");

        if (def.getLinearDamping() < 0)
            throw new IllegalArgumentException("Linear damping is invalid");
    }

    public float getTorque() {
        return m_torque;
    }

    public void setTorque(float torque) {
        m_torque = torque;
    }

    /**
     * Set entity to which this body belongs.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * @return entity to which this body belongs
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @return reference to the underlying list of fixtures attached to this body
     */
    public List<Fixture> getFixtures() {
        return fixtures;
    }

    /**
     * Creates a fixture and attach it to this body.
     * Use this function if you need to set some fixture parameters, like friction.
     * Otherwise you can create the fixture directly from a shape.
     * If the density is non-zero, this function automatically updates the mass of the body.
     * Contacts are not created until the next time step.
     * Note: This function is locked during callbacks.
     *
     * @param def the fixture definition
     */
    public Fixture createFixture(FixtureDef def) {
        world.assertNotLocked();

        Fixture fixture = new Fixture(this, def);

        if ((m_flags & e_activeFlag) == e_activeFlag) {
            BroadPhase broadPhase = world.getContactManager().broadPhase;
            fixture.createProxies(broadPhase, m_xf);
        }

        fixtures.add(fixture);

        // Adjust mass properties if needed.
        if (fixture.getDensity() > 0) {
            resetMassData();
        }

        // Let the world know we have a new fixture. This will cause new contacts
        // to be created at the beginning of the next time step.
        world.notifyNewFixture();

        return fixture;
    }

    private final FixtureDef fixDef = new FixtureDef();

    /**
     * Creates a fixture from a shape and attach it to this body.
     * This is a convenience function.
     * Use FixtureDef if you need to set parameters like friction, restitution, user data, or filtering.
     * If the density is non-zero, this function automatically updates the mass of the body.
     * Note: this function is locked during callbacks.
     *
     *
     * @param shape the shape to be cloned.
     * @param density the shape density (set to zero for static bodies).
     */
    public Fixture createFixture(Shape shape, float density) {
        fixDef.setShape(shape);
        fixDef.setDensity(density);

        return createFixture(fixDef);
    }

    /**
     * Destroy a fixture.
     * This removes the fixture from the broad-phase and destroys all contacts
     * associated with this fixture.
     * This will automatically adjust the mass of the body if the body
     * is dynamic and the fixture has positive density.
     * All fixtures attached to a body are implicitly destroyed when the body is destroyed.
     * Note: this function is locked during callbacks.
     *
     * @param fixture the fixture to be removed
     */
    public void destroyFixture(Fixture fixture) {
        world.assertNotLocked();

        assert fixture.getBody() == this;
        assert fixtures.contains(fixture);

        fixtures.remove(fixture);

        // Destroy any contacts associated with the fixture.
        ContactEdge edge = m_contactList;
        while (edge != null) {
            Contact c = edge.contact;
            edge = edge.next;

            Fixture fixtureA = c.getFixtureA();
            Fixture fixtureB = c.getFixtureB();

            if (fixture == fixtureA || fixture == fixtureB) {
                // This destroys the contact and removes it from this body's contact list.
                world.getContactManager().destroy(c);
            }
        }

        if ((m_flags & e_activeFlag) == e_activeFlag) {
            BroadPhase broadPhase = world.getContactManager().broadPhase;
            fixture.destroyProxies(broadPhase);
        }

        fixture.destroy();

        resetMassData();
    }

    /**
     * Set the position of the body's origin and rotation.
     * This breaks any contacts and wakes the other bodies.
     * Manipulating a body's transform may cause non-physical behavior.
     * Note: contacts are updated on the next call to World.step().
     *
     * @param position the world position of the body's local origin
     * @param angle the world rotation in radians
     */
    public void setTransform(Vec2 position, float angle) {
        world.assertNotLocked();

        m_xf.q.set(angle);
        m_xf.p.set(position);

        // m_sweep.c0 = m_sweep.c = Mul(m_xf, m_sweep.localCenter);
        Transform.mulToOutUnsafe(m_xf, m_sweep.localCenter, m_sweep.c);
        m_sweep.a = angle;

        m_sweep.c0.set(m_sweep.c);
        m_sweep.a0 = m_sweep.a;

        BroadPhase broadPhase = world.getContactManager().broadPhase;
        for (Fixture f : fixtures) {
            f.synchronize(broadPhase, m_xf, m_xf);
        }
    }

    /**
     * @return the world transform of the body's origin.
     */
    public Transform getTransform() {
        return m_xf;
    }

    /**
     * Get the world body origin position. Do not modify.
     *
     * @return the world position of the body's origin.
     */
    public Vec2 getPosition() {
        return m_xf.p;
    }

    /**
     * Get the angle in radians.
     *
     * @return the current world rotation angle in radians.
     */
    public float getAngle() {
        return m_sweep.a;
    }

    /**
     * Get the world position of the center of mass. Do not modify.
     */
    public Vec2 getWorldCenter() {
        return m_sweep.c;
    }

    /**
     * Get the local position of the center of mass. Do not modify.
     */
    public Vec2 getLocalCenter() {
        return m_sweep.localCenter;
    }

    /**
     * Set the linear velocity of the center of mass.
     *
     * @param v the new linear velocity of the center of mass.
     */
    public void setLinearVelocity(Vec2 v) {
        if (type == BodyType.STATIC) {
            return;
        }

        if (Vec2.dot(v, v) > 0) {
            setAwake(true);
        }

        m_linearVelocity.set(v);
    }

    /**
     * Get the linear velocity of the center of mass. Do not modify, instead use
     * {@link #setLinearVelocity(Vec2)}.
     *
     * @return the linear velocity of the center of mass.
     */
    public Vec2 getLinearVelocity() {
        return m_linearVelocity;
    }

    /**
     * Set the angular velocity.
     *
     * @param w the new angular velocity in radians/second.
     */
    public void setAngularVelocity(float w) {
        if (type == BodyType.STATIC) {
            return;
        }

        if (w * w > 0f) {
            setAwake(true);
        }

        m_angularVelocity = w;
    }

    void setAngularVelocityDirectly(float angularVelocity) {
        this.m_angularVelocity = angularVelocity;
    }

    /**
     * Get the angular velocity.
     *
     * @return the angular velocity in radians/second.
     */
    public float getAngularVelocity() {
        return m_angularVelocity;
    }

    /**
     * @return the gravity scale of the body
     */
    public float getGravityScale() {
        return gravityScale;
    }

    /**
     * Set the gravity scale of the body.
     *
     * @param gravityScale gravity scale
     */
    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
    }

    /**
     * Apply a force at a world point.
     * If the force is not applied at the center of mass, it will generate a torque and affect the angular velocity.
     * This wakes up the body.
     *
     * @param force the world force vector, usually in Newtons (N)
     * @param point the world position of the point of application
     */
    public void applyForce(Vec2 force, Vec2 point) {
        applyForceToCenter(force);

        m_torque += (point.x - m_sweep.c.x) * force.y - (point.y - m_sweep.c.y) * force.x;
    }

    /**
     * Apply a force to the center of mass.
     * This wakes up the body.
     *
     * @param force the world force vector, usually in Newtons (N)
     */
    public void applyForceToCenter(Vec2 force) {
        if (type != BodyType.DYNAMIC) {
            return;
        }

        if (!isAwake()) {
            setAwake(true);
        }

        m_force.addLocal(force);
    }

    /**
     * Apply a torque.
     * This affects the angular velocity without affecting the linear velocity of the center of mass.
     * This wakes up the body.
     *
     * @param torque about the z-axis (out of the screen), usually in N-m
     */
    public void applyTorque(float torque) {
        if (type != BodyType.DYNAMIC) {
            return;
        }

        if (!isAwake()) {
            setAwake(true);
        }

        m_torque += torque;
    }

    /**
     * Apply an impulse at a point.
     * This immediately modifies the velocity.
     * It also modifies the angular velocity if the point of application is not at the center of mass.
     * This wakes up the body if 'wake' is set to true.
     * If the body is sleeping and 'wake' is false, then there is no effect.
     *
     * @param impulse the world impulse vector, usually in N-seconds or kg-m/s
     * @param point the world position of the point of application
     * @param wake also wake up the body
     */
    public void applyLinearImpulse(Vec2 impulse, Vec2 point, boolean wake) {
        if (type != BodyType.DYNAMIC) {
            return;
        }

        if (!isAwake()) {
            if (wake) {
                setAwake(true);
            } else {
                return;
            }
        }

        m_linearVelocity.x += impulse.x * m_invMass;
        m_linearVelocity.y += impulse.y * m_invMass;

        m_angularVelocity += m_invI * ((point.x - m_sweep.c.x) * impulse.y - (point.y - m_sweep.c.y) * impulse.x);
    }

    /**
     * Apply an angular impulse.
     *
     * @param impulse the angular impulse in units of kg*m*m/s
     */
    public void applyAngularImpulse(float impulse) {
        if (type != BodyType.DYNAMIC) {
            return;
        }

        if (!isAwake()) {
            setAwake(true);
        }

        m_angularVelocity += m_invI * impulse;
    }

    /**
     * @return total mass of the body, usually in kilograms (kg)
     */
    public float getMass() {
        return m_mass;
    }

    /**
     * @return central rotational inertia of the body, usually in kg-m^2
     */
    public float getInertia() {
        return m_I + m_mass * Vec2.dot(m_sweep.localCenter, m_sweep.localCenter);
    }

    /**
     * Get the mass data of the body.
     * The rotational inertia is relative to the center of mass.
     * Fills in a struct containing the mass, inertia and center of the body.
     */
    public void getMassData(MassData data) {
        data.mass = m_mass;
        data.I = getInertia();
        data.center.set(m_sweep.localCenter);
    }

    /**
     * Set the mass properties to override the mass properties of the fixtures.
     * Note that this changes the center of mass position.
     * Note that creating or destroying fixtures can also alter the mass.
     * This function has no effect if the body isn't dynamic.
     *
     * @param massData the mass properties.
     */
    public void setMassData(MassData massData) {
        world.assertNotLocked();

        if (type != BodyType.DYNAMIC) {
            return;
        }

        m_invMass = 0;
        m_I = 0;
        m_invI = 0;

        m_mass = massData.mass;
        if (m_mass <= 0) {
            m_mass = 1;
        }

        m_invMass = 1.0f / m_mass;

        if (massData.I > 0 && (m_flags & e_fixedRotationFlag) == 0) {
            m_I = massData.I - m_mass * Vec2.dot(massData.center, massData.center);
            assert m_I > 0;
            m_invI = 1.0f / m_I;
        }

        final Vec2 oldCenter = world.getPool().popVec2();
        // Move center of mass.
        oldCenter.set(m_sweep.c);
        m_sweep.localCenter.set(massData.center);
        // m_sweep.c0 = m_sweep.c = Mul(m_xf, m_sweep.localCenter);
        Transform.mulToOutUnsafe(m_xf, m_sweep.localCenter, m_sweep.c0);
        m_sweep.c.set(m_sweep.c0);

        // Update center of mass velocity.
        // m_linearVelocity += Cross(m_angularVelocity, m_sweep.c - oldCenter);
        final Vec2 temp = world.getPool().popVec2();
        temp.set(m_sweep.c).subLocal(oldCenter);

        // crossToOut(m_angularVelocity, temp, temp);
        final float tempY = m_angularVelocity * temp.x;
        temp.x = -m_angularVelocity * temp.y;
        temp.y = tempY;

        m_linearVelocity.addLocal(temp);

        world.getPool().pushVec2(2);
    }

    private final MassData pmd = new MassData();

    /**
     * This resets the mass properties to the sum of the mass properties of the fixtures.
     * This normally does not need to be called unless you called setMassData to override the mass and you
     * later want to reset the mass.
     */
    public void resetMassData() {
        // Compute mass data from shapes. Each shape has its own density.
        m_mass = 0;
        m_invMass = 0;
        m_I = 0;
        m_invI = 0;
        m_sweep.localCenter.setZero();

        // Static and kinematic bodies have zero mass.
        if (type == BodyType.STATIC || type == BodyType.KINEMATIC) {
            m_sweep.c0.set(m_xf.p);
            m_sweep.c.set(m_xf.p);
            m_sweep.a0 = m_sweep.a;
            return;
        }

        assert type == BodyType.DYNAMIC;

        // Accumulate mass over all fixtures.
        final Vec2 localCenter = world.getPool().popVec2();
        localCenter.setZero();
        final Vec2 temp = world.getPool().popVec2();
        final MassData massData = pmd;

        for (Fixture f : fixtures) {
            if (f.getDensity() == 0.0f) {
                continue;
            }
            f.getMassData(massData);
            m_mass += massData.mass;
            // center += massData.mass * massData.center;
            temp.set(massData.center).mulLocal(massData.mass);
            localCenter.addLocal(temp);
            m_I += massData.I;
        }

        // Compute center of mass.
        if (m_mass > 0.0f) {
            m_invMass = 1.0f / m_mass;
            localCenter.mulLocal(m_invMass);
        } else {
            // Force all dynamic bodies to have a positive mass.
            m_mass = 1.0f;
            m_invMass = 1.0f;
        }

        if (m_I > 0.0f && (m_flags & e_fixedRotationFlag) == 0) {
            // Center the inertia about the center of mass.
            m_I -= m_mass * Vec2.dot(localCenter, localCenter);
            assert m_I > 0.0f;
            m_invI = 1.0f / m_I;
        } else {
            m_I = 0.0f;
            m_invI = 0.0f;
        }

        Vec2 oldCenter = world.getPool().popVec2();
        // Move center of mass.
        oldCenter.set(m_sweep.c);
        m_sweep.localCenter.set(localCenter);
        // m_sweep.c0 = m_sweep.c = Mul(m_xf, m_sweep.localCenter);
        Transform.mulToOutUnsafe(m_xf, m_sweep.localCenter, m_sweep.c0);
        m_sweep.c.set(m_sweep.c0);

        // Update center of mass velocity.
        // m_linearVelocity += Cross(m_angularVelocity, m_sweep.c - oldCenter);
        temp.set(m_sweep.c).subLocal(oldCenter);

        final Vec2 temp2 = oldCenter;
        Vec2.crossToOutUnsafe(m_angularVelocity, temp, temp2);
        m_linearVelocity.addLocal(temp2);

        world.getPool().pushVec2(3);
    }

    /**
     * Get the world coordinates of a point given the local coordinates.
     *
     * @param localPoint a point on the body measured relative the the body's origin.
     * @return the same point expressed in world coordinates.
     */
    public Vec2 getWorldPoint(Vec2 localPoint) {
        Vec2 v = new Vec2();
        getWorldPointToOut(localPoint, v);
        return v;
    }

    public void getWorldPointToOut(Vec2 localPoint, Vec2 out) {
        Transform.mulToOut(m_xf, localPoint, out);
    }

    /**
     * Get the world coordinates of a vector given the local coordinates.
     *
     * @param localVector a vector fixed in the body.
     * @return the same vector expressed in world coordinates.
     */
    public Vec2 getWorldVector(Vec2 localVector) {
        Vec2 out = new Vec2();
        getWorldVectorToOut(localVector, out);
        return out;
    }

    public void getWorldVectorToOut(Vec2 localVector, Vec2 out) {
        Rotation.mulToOut(m_xf.q, localVector, out);
    }

    public void getWorldVectorToOutUnsafe(Vec2 localVector, Vec2 out) {
        Rotation.mulToOutUnsafe(m_xf.q, localVector, out);
    }

    /**
     * Gets a local point relative to the body's origin given a world point.
     *
     * @param worldPoint point in world coordinates.
     * @return the corresponding local point relative to the body's origin.
     */
    public Vec2 getLocalPoint(Vec2 worldPoint) {
        Vec2 out = new Vec2();
        getLocalPointToOut(worldPoint, out);
        return out;
    }

    public void getLocalPointToOut(Vec2 worldPoint, Vec2 out) {
        Transform.mulTransToOut(m_xf, worldPoint, out);
    }

    /**
     * Gets a local vector given a world vector.
     *
     * @param worldVector vector in world coordinates.
     * @return the corresponding local vector.
     */
    public Vec2 getLocalVector(Vec2 worldVector) {
        Vec2 out = new Vec2();
        getLocalVectorToOut(worldVector, out);
        return out;
    }

    public void getLocalVectorToOut(Vec2 worldVector, Vec2 out) {
        Rotation.mulTrans(m_xf.q, worldVector, out);
    }

    public void getLocalVectorToOutUnsafe(Vec2 worldVector, Vec2 out) {
        Rotation.mulTransUnsafe(m_xf.q, worldVector, out);
    }

    /**
     * Get the world linear velocity of a world point attached to this body.
     *
     * @param worldPoint point in world coordinates.
     * @return the world velocity of a point.
     */
    public Vec2 getLinearVelocityFromWorldPoint(Vec2 worldPoint) {
        Vec2 out = new Vec2();
        getLinearVelocityFromWorldPointToOut(worldPoint, out);
        return out;
    }

    public void getLinearVelocityFromWorldPointToOut(Vec2 worldPoint, Vec2 out) {
        final float tempX = worldPoint.x - m_sweep.c.x;
        final float tempY = worldPoint.y - m_sweep.c.y;
        out.x = -m_angularVelocity * tempY + m_linearVelocity.x;
        out.y = m_angularVelocity * tempX + m_linearVelocity.y;
    }

    /**
     * Get the world velocity of a local point.
     *
     * @param localPoint point in local coordinates.
     * @return the world velocity of a point.
     */
    public Vec2 getLinearVelocityFromLocalPoint(Vec2 localPoint) {
        Vec2 out = new Vec2();
        getLinearVelocityFromLocalPointToOut(localPoint, out);
        return out;
    }

    public void getLinearVelocityFromLocalPointToOut(Vec2 localPoint, Vec2 out) {
        getWorldPointToOut(localPoint, out);
        getLinearVelocityFromWorldPointToOut(out, out);
    }

    /**
     * Get the linear damping of the body.
     **/
    public float getLinearDamping() {
        return linearDamping;
    }

    /**
     * Set the linear damping of the body.
     **/
    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    /**
     * Get the angular damping of the body.
     **/
    public float getAngularDamping() {
        return angularDamping;
    }

    /**
     * Set the angular damping of the body.
     **/
    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getSleepTime() {
        return sleepTime;
    }

    void setSleepTime(float sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * @return body type
     */
    public BodyType getType() {
        return type;
    }

    /**
     * Set the type of this body.
     * This may alter the mass and velocity.
     *
     * @param type body type
     */
    public void setType(BodyType type) {
        world.assertNotLocked();

        if (this.type == type) {
            return;
        }

        this.type = type;

        resetMassData();

        if (this.type == BodyType.STATIC) {
            m_linearVelocity.setZero();
            m_angularVelocity = 0.0f;
            m_sweep.a0 = m_sweep.a;
            m_sweep.c0.set(m_sweep.c);
            synchronizeFixtures();
        }

        setAwake(true);

        m_force.setZero();
        m_torque = 0.0f;

        destroyAttachedContacts();

        // Touch the proxies so that new contacts will be created (when appropriate)
        BroadPhase broadPhase = world.getContactManager().broadPhase;
        for (Fixture f : fixtures) {
            int proxyCount = f.getProxyCount();
            for (int i = 0; i < proxyCount; ++i) {
                broadPhase.touchProxy(f.getProxyId(i));
            }
        }
    }

    void destroy() {
        destroyAttachedJoints();

        destroyAttachedContacts();

        destroyFixtures();
    }

    private void destroyAttachedJoints() {
        JointEdge je = this.m_jointList;
        while (je != null) {
            JointEdge je0 = je;
            je = je.next;

            if (world.getDestructionListener() != null) {
                world.getDestructionListener().onDestroy(je0.joint);
            }

            world.destroyJoint(je0.joint);

            this.m_jointList = je;
        }

        this.m_jointList = null;
    }

    private void destroyAttachedContacts() {
        // Delete the attached contacts.
        ContactEdge ce = m_contactList;
        while (ce != null) {
            ContactEdge ce0 = ce;
            ce = ce.next;
            world.getContactManager().destroy(ce0.contact);
        }
        m_contactList = null;
    }

    private void destroyFixtures() {
        for (Fixture f : getFixtures()) {
            if (world.getDestructionListener() != null) {
                world.getDestructionListener().onDestroy(f);
            }

            f.destroyProxies(world.getContactManager().broadPhase);
            f.destroy();

            // jbox2dTODO djm recycle fixtures (here or in that destroy method)
        }

        getFixtures().clear();
    }

    /**
     * @return is this body treated like a bullet for continuous collision detection?
     **/
    public boolean isBullet() {
        return (m_flags & e_bulletFlag) == e_bulletFlag;
    }

    /**
     * Should this body be treated like a bullet for continuous collision detection?
     **/
    public void setBullet(boolean flag) {
        if (flag) {
            m_flags |= e_bulletFlag;
        } else {
            m_flags &= ~e_bulletFlag;
        }
    }

    /**
     * You can disable sleeping on this body.
     * If you disable sleeping, the body will be woken.
     *
     * @param flag sleep flag
     */
    public void setSleepingAllowed(boolean flag) {
        if (flag) {
            m_flags |= e_autoSleepFlag;
        } else {
            m_flags &= ~e_autoSleepFlag;
            setAwake(true);
        }
    }

    /**
     * @return whether this body is allowed to sleep
     */
    public boolean isSleepingAllowed() {
        return (m_flags & e_autoSleepFlag) == e_autoSleepFlag;
    }

    /**
     * Set the sleep state of the body.
     * A sleeping body has very low CPU cost.
     *
     * @param flag set to true to put body to sleep, false to wake it
     */
    public void setAwake(boolean flag) {
        if (flag) {
            if ((m_flags & e_awakeFlag) == 0) {
                m_flags |= e_awakeFlag;
                sleepTime = 0.0f;
            }
        } else {
            m_flags &= ~e_awakeFlag;
            sleepTime = 0.0f;
            m_linearVelocity.setZero();
            m_angularVelocity = 0.0f;
            m_force.setZero();
            m_torque = 0.0f;
        }
    }

    /**
     * Get the sleeping state of this body.
     *
     * @return true if the body is awake.
     */
    public boolean isAwake() {
        return (m_flags & e_awakeFlag) == e_awakeFlag;
    }

    /**
     * Set the active state of the body.
     * An inactive body is not simulated and cannot be collided with or woken up.
     * If you pass a flag of true, all fixtures will be added to the broad-phase.
     * If you pass a flag of false, all fixtures will be removed from the broad-phase and all contacts will be destroyed.
     * Fixtures and joints are otherwise unaffected.
     * You may continue to create/destroy fixtures and joints on inactive bodies.
     * Fixtures on an inactive body are implicitly inactive
     * and will not participate in collisions, ray-casts, or queries.
     * Joints connected to an inactive body are implicitly inactive.
     * An inactive body is still owned by a World object and remains in the body list.
     *
     * @param flag active flag
     */
    public void setActive(boolean flag) {
        world.assertNotLocked();

        if (flag == isActive()) {
            return;
        }

        if (flag) {
            m_flags |= e_activeFlag;

            // Create all proxies.
            BroadPhase broadPhase = world.getContactManager().broadPhase;
            for (Fixture f : fixtures) {
                f.createProxies(broadPhase, m_xf);
            }

            // Contacts are created the next time step.
        } else {
            m_flags &= ~e_activeFlag;

            // Destroy all proxies.
            BroadPhase broadPhase = world.getContactManager().broadPhase;
            for (Fixture f : fixtures) {
                f.destroyProxies(broadPhase);
            }

            destroyAttachedContacts();
        }
    }

    /**
     * @return the active state of the body
     */
    public boolean isActive() {
        return (m_flags & e_activeFlag) == e_activeFlag;
    }

    /**
     * Set this body to have fixed rotation. This causes the mass to be reset.
     *
     * @param flag fixed rotation flag
     */
    public void setFixedRotation(boolean flag) {
        if (flag) {
            m_flags |= e_fixedRotationFlag;
        } else {
            m_flags &= ~e_fixedRotationFlag;
        }

        resetMassData();
    }

    /**
     * @return does this body have fixed rotation
     */
    public boolean isFixedRotation() {
        return (m_flags & e_fixedRotationFlag) == e_fixedRotationFlag;
    }

    /**
     * Get the list of all joints attached to this body.
     **/
    public JointEdge getJointList() {
        return m_jointList;
    }

    /**
     * Get the list of all contacts attached to this body.
     * Note: this list changes during the time step and you may miss some collisions if you don't
     * use ContactListener.
     */
    public ContactEdge getContactList() {
        return m_contactList;
    }

    /**
     * Get the user data pointer that was provided in the body definition.
     **/
    public Object getUserData() {
        return userData;
    }

    /**
     * Set the user data. Use this to store your application specific data.
     */
    public void setUserData(Object data) {
        userData = data;
    }

    /**
     * Get the parent world of this body.
     */
    public World getWorld() {
        return world;
    }

    // djm pooling
    private final Transform pxf = new Transform();

    void synchronizeFixtures() {
        final Transform xf1 = pxf;
        // xf1.position = m_sweep.c0 - Mul(xf1.R, m_sweep.localCenter);

        // xf1.q.set(m_sweep.a0);
        // Rot.mulToOutUnsafe(xf1.q, m_sweep.localCenter, xf1.p);
        // xf1.p.mulLocal(-1).addLocal(m_sweep.c0);
        // inlined:
        xf1.q.s = JBoxUtils.sin(m_sweep.a0);
        xf1.q.c = JBoxUtils.cos(m_sweep.a0);
        xf1.p.x = m_sweep.c0.x - xf1.q.c * m_sweep.localCenter.x + xf1.q.s * m_sweep.localCenter.y;
        xf1.p.y = m_sweep.c0.y - xf1.q.s * m_sweep.localCenter.x - xf1.q.c * m_sweep.localCenter.y;
        // end inline

        for (Fixture f : fixtures) {
            f.synchronize(world.getContactManager().broadPhase, xf1, m_xf);
        }
    }

    void synchronizeTransform() {
        // m_xf.q.set(m_sweep.a);
        //
        // // m_xf.position = m_sweep.c - Mul(m_xf.R, m_sweep.localCenter);
        // Rot.mulToOutUnsafe(m_xf.q, m_sweep.localCenter, m_xf.p);
        // m_xf.p.mulLocal(-1).addLocal(m_sweep.c);
        //
        m_xf.q.s = JBoxUtils.sin(m_sweep.a);
        m_xf.q.c = JBoxUtils.cos(m_sweep.a);
        Rotation q = m_xf.q;
        Vec2 v = m_sweep.localCenter;
        m_xf.p.x = m_sweep.c.x - q.c * v.x + q.s * v.y;
        m_xf.p.y = m_sweep.c.y - q.s * v.x - q.c * v.y;
    }

    /**
     * This is used to prevent connected bodies from colliding.
     * It may lie, depending on the collideConnected flag.
     *
     * @param other other body
     * @return if they should collide
     */
    public boolean shouldCollide(Body other) {
        // At least one body should be dynamic.
        if (type != BodyType.DYNAMIC && other.type != BodyType.DYNAMIC) {
            return false;
        }

        // Does a joint prevent collision?
        for (JointEdge jn = m_jointList; jn != null; jn = jn.next) {
            if (jn.other == other && !jn.joint.getCollideConnected()) {
                return false;
            }
        }

        return true;
    }

    void advance(float t) {
        // Advance to the new safe time. This doesn't sync the broad-phase.
        m_sweep.advance(t);
        m_sweep.c.set(m_sweep.c0);
        m_sweep.a = m_sweep.a0;
        m_xf.q.set(m_sweep.a);
        // m_xf.position = m_sweep.c - Mul(m_xf.R, m_sweep.localCenter);
        Rotation.mulToOutUnsafe(m_xf.q, m_sweep.localCenter, m_xf.p);
        m_xf.p.mulLocal(-1).addLocal(m_sweep.c);
    }
}