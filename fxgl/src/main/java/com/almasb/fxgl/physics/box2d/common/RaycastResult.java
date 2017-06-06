/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.common;

// updated to rev 100

import com.almasb.fxgl.core.math.Vec2;

public class RaycastResult {
    public float lambda = 0.0f;
    public final Vec2 normal = new Vec2();

    public RaycastResult set(RaycastResult argOther) {
        lambda = argOther.lambda;
        normal.set(argOther.normal);
        return this;
    }
}
