/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.core.math.Vec2;

// updated to rev 100

/**
 * Ray-cast input data. The ray extends from p1 to p1 + maxFraction * (p2 - p1).
 */
public class RayCastInput {
    public final Vec2 p1, p2;
    public float maxFraction;

    public RayCastInput() {
        p1 = new Vec2();
        p2 = new Vec2();
        maxFraction = 0;
    }

    public void set(final RayCastInput rci) {
        p1.set(rci.p1);
        p2.set(rci.p2);
        maxFraction = rci.maxFraction;
    }
}
