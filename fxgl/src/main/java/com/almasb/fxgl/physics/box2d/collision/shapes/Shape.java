/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision.shapes;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.AABB;
import com.almasb.fxgl.physics.box2d.collision.RayCastInput;
import com.almasb.fxgl.physics.box2d.collision.RayCastOutput;
import com.almasb.fxgl.physics.box2d.common.Transform;

/**
 * A shape is used for collision detection.
 * You can create a shape however you like.
 * Shapes used for simulation in World are created automatically when a Fixture is created.
 * Shapes may encapsulate one or more child shapes.
 */
public abstract class Shape {

    private final ShapeType type;

    private float radius;

    public Shape(ShapeType type, float radius) {
        this.type = type;
        this.radius = radius;
    }

    @Override
    public abstract Shape clone();

    /**
     * Get the type of this shape. You can use this to down cast to the concrete shape.
     *
     * @return the shape type
     */
    public ShapeType getType() {
        return type;
    }

    /**
     * The radius of the underlying shape. This can refer to different things depending on the shape
     * implementation.
     *
     * @return shape radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the underlying shape. This can refer to different things depending on the
     * implementation.
     *
     * @param radius shape radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * @return the number of child primitives
     */
    public abstract int getChildCount();

    /**
     * Test a point for containment in this shape. This only works for convex shapes.
     *
     * @param xf the shape world transform.
     * @param p a point in world coordinates.
     */
    public abstract boolean testPoint(final Transform xf, final Vec2 p);

    /**
     * Cast a ray against a child shape.
     *
     * @param output the ray-cast results.
     * @param input the ray-cast input parameters.
     * @param transform the transform to be applied to the shape.
     * @param childIndex the child shape index
     * @return if hit
     */
    public abstract boolean raycast(RayCastOutput output, RayCastInput input, Transform transform,
                                    int childIndex);


    /**
     * Given a transform, compute the associated axis aligned bounding box for a child shape.
     *
     * @param aabb returns the axis aligned box
     * @param xf the world transform of the shape
     * @param childIndex the child shape index
     */
    public abstract void computeAABB(final AABB aabb, final Transform xf, int childIndex);

    /**
     * Compute the mass properties of this shape using its dimensions and density. The inertia tensor
     * is computed about the local origin.
     *
     * @param massData returns the mass data for this shape.
     * @param density the density in kilograms per meter squared.
     */
    public abstract void computeMass(final MassData massData, final float density);

    /**
     * Compute the distance from the current shape to the specified point. This only works for convex
     * shapes.
     *
     * @param xf the shape world transform.
     * @param p a point in world coordinates.
     * @param normalOut returns the direction in which the distance increases.
     * @return the distance from the current shape.
     */
    public abstract float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut);
}
