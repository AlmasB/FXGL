/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.particle;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape;

import java.util.Set;

/**
 * A particle group definition holds all the data needed to construct a particle group. You can
 * safely re-use these definitions.
 */
public class ParticleGroupDef {

    private int typeFlags = ParticleTypeInternal.b2_waterParticle;   // 0

    /**
     * @return the particle-behavior flags from {@link ParticleTypeInternal}
     */
    public int getTypeFlags() {
        return typeFlags;
    }

    /**
     * Set particle types. E.g. EnumSet.of(ParticleType...).
     *
     * @param types particle types
     */
    public void setTypes(Set<ParticleType> types) {
        for (ParticleType type : types) {
            typeFlags |= type.bit;
        }
    }

    private int groupFlags = 0;

    /**
     * @return the group-construction flags
     */
    public int getGroupFlags() {
        return groupFlags;
    }

    public void setGroupFlags(int groupFlags) {
        this.groupFlags = groupFlags;
    }

    private final Vec2 position = new Vec2();

    /**
     * @return the world position of the group.
     */
    public Vec2 getPosition() {
        return position;
    }

    /**
     * Moves the group's shape a distance equal to the value of
     * position.
     *
     * @param x x coord
     * @param y y coord
     */
    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    private float angle = 0;

    /**
     * @return The world angle of the group in radians. Rotates the shape by an angle equal to the value of
     * angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * The world angle of the group in radians. Rotates the shape by an angle equal to the value of
     * angle.
     *
     * @param angle in radians
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * The linear velocity of the group's origin in world co-ordinates.
     */
    private final Vec2 linearVelocity = new Vec2();

    public Vec2 getLinearVelocity() {
        return linearVelocity;
    }

    /**
     * The angular velocity of the group.
     */
    private float angularVelocity = 0;

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    /**
     * The color of all particles in the group.
     */
    private ParticleColor color = null;

    public ParticleColor getColor() {
        return color;
    }

    public void setColor(ParticleColor color) {
        this.color = color;
    }

    /**
     * The strength of cohesion among the particles in a group with flag b2_elasticParticle or
     * b2_springParticle.
     */
    private float strength = 1;

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    private Shape shape = null;

    /**
     * @return shape containing the particle group
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * @param shape the shape containing the particle group
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * If true, destroy the group automatically after its last particle has been destroyed.
     */
    private boolean destroyAutomatically = true;

    public boolean isDestroyAutomatically() {
        return destroyAutomatically;
    }

    public void setDestroyAutomatically(boolean destroyAutomatically) {
        this.destroyAutomatically = destroyAutomatically;
    }

    /**
     * Use this to store application-specific group data.
     */
    private Object userData = null;

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }
}
