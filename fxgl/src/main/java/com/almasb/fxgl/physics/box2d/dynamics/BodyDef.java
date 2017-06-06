/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.core.math.Vec2;

/**
 * A body definition holds all the data needed to construct a rigid body. You can safely re-use body
 * definitions. Shapes are added to a body after construction.
 *
 * @author daniel
 */
public class BodyDef {

    /**
     * The body type: static, kinematic, or dynamic. Note: if a dynamic body would have zero mass, the
     * mass is set to one.
     */
    private BodyType type = BodyType.STATIC;

    /**
     * Use this to store application specific body data.
     */
    private Object userData = null;

    /**
     * The world position of the body. Avoid creating bodies at the origin since this can lead to many
     * overlapping shapes.
     */
    private Vec2 position = new Vec2();

    /**
     * The world angle of the body in radians.
     */
    private float angle = 0;

    /**
     * The linear velocity of the body in world co-ordinates.
     */
    private Vec2 linearVelocity = new Vec2();

    /**
     * The angular velocity of the body.
     */
    private float angularVelocity = 0;

    /**
     * Linear damping is use to reduce the linear velocity. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     */
    private float linearDamping = 0;

    /**
     * Angular damping is use to reduce the angular velocity. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     */
    private float angularDamping = 0;

    /**
     * Set this flag to false if this body should never fall asleep. Note that this increases CPU
     * usage.
     */
    private boolean allowSleep = true;

    /**
     * Is this body initially sleeping?
     */
    private boolean awake = true;

    /**
     * Should this body be prevented from rotating? Useful for characters.
     */
    private boolean fixedRotation = false;

    /**
     * Is this a fast moving body that should be prevented from tunneling through other moving bodies?
     * Note that all bodies are prevented from tunneling through kinematic and static bodies. This
     * setting is only considered on dynamic bodies.
     * You should use this flag sparingly since it increases processing time.
     */
    private boolean bullet = false;

    /**
     * Does this body start out active?
     */
    private boolean active = true;

    /**
     * Experimental: scales the inertia tensor.
     */
    private float gravityScale = 1;

    /**
     * The body type: static, kinematic, or dynamic. Note: if a dynamic body would have zero mass, the
     * mass is set to one.
     */
    public BodyType getType() {
        return type;
    }

    /**
     * The body type: static, kinematic, or dynamic. Note: if a dynamic body would have zero mass, the
     * mass is set to one.
     */
    public void setType(BodyType type) {
        this.type = type;
    }

    /**
     * Use this to store application specific body data.
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * Use this to store application specific body data.
     */
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    /**
     * The world position of the body. Avoid creating bodies at the origin since this can lead to many
     * overlapping shapes.
     */
    public Vec2 getPosition() {
        return position;
    }

    /**
     * The world position of the body. Avoid creating bodies at the origin since this can lead to many
     * overlapping shapes.
     */
    public void setPosition(Vec2 position) {
        this.position = position;
    }

    /**
     * The world angle of the body in radians.
     */
    public float getAngle() {
        return angle;
    }

    /**
     * The world angle of the body in radians.
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * The linear velocity of the body in world co-ordinates.
     */
    public Vec2 getLinearVelocity() {
        return linearVelocity;
    }

    /**
     * The linear velocity of the body in world co-ordinates.
     */
    public void setLinearVelocity(Vec2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    /**
     * The angular velocity of the body.
     */
    public float getAngularVelocity() {
        return angularVelocity;
    }

    /**
     * The angular velocity of the body.
     */
    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    /**
     * Linear damping is use to reduce the linear velocity. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     */
    public float getLinearDamping() {
        return linearDamping;
    }

    /**
     * Linear damping is use to reduce the linear velocity. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     */
    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    /**
     * Angular damping is use to reduce the angular velocity. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     */
    public float getAngularDamping() {
        return angularDamping;
    }

    /**
     * Angular damping is use to reduce the angular velocity. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     */
    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    /**
     * Set this flag to false if this body should never fall asleep. Note that this increases CPU
     * usage.
     */
    public boolean isAllowSleep() {
        return allowSleep;
    }

    /**
     * Set this flag to false if this body should never fall asleep. Note that this increases CPU
     * usage.
     */
    public void setAllowSleep(boolean allowSleep) {
        this.allowSleep = allowSleep;
    }

    /**
     * Is this body initially sleeping?
     */
    public boolean isAwake() {
        return awake;
    }

    /**
     * Is this body initially sleeping?
     */
    public void setAwake(boolean awake) {
        this.awake = awake;
    }

    /**
     * Should this body be prevented from rotating? Useful for characters.
     */
    public boolean isFixedRotation() {
        return fixedRotation;
    }

    /**
     * Should this body be prevented from rotating? Useful for characters.
     */
    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    /**
     * Is this a fast moving body that should be prevented from tunneling through other moving bodies?
     * Note that all bodies are prevented from tunneling through kinematic and static bodies. This
     * setting is only considered on dynamic bodies.
     * You should use this flag sparingly since it increases processing time.
     */
    public boolean isBullet() {
        return bullet;
    }

    /**
     * Is this a fast moving body that should be prevented from tunneling through other moving bodies?
     * Note that all bodies are prevented from tunneling through kinematic and static bodies. This
     * setting is only considered on dynamic bodies.
     * You should use this flag sparingly since it increases processing time.
     */
    public void setBullet(boolean bullet) {
        this.bullet = bullet;
    }

    /**
     * Does this body start out active?
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Does this body start out active?
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Experimental: scales the inertia tensor.
     */
    public float getGravityScale() {
        return gravityScale;
    }

    /**
     * Experimental: scales the inertia tensor.
     */
    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
    }
}
