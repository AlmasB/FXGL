/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.core.math.Vec2;

/**
 * Output for Distance.
 * @author Daniel
 */
public class DistanceOutput {
    /** Closest point on shapeA */
    public final Vec2 pointA = new Vec2();

    /** Closest point on shapeB */
    public final Vec2 pointB = new Vec2();

    public float distance;

    /** number of gjk iterations used */
    public int iterations;
}
