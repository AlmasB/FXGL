/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.physics.box2d.collision.Distance.DistanceProxy;
import com.almasb.fxgl.physics.box2d.common.Transform;

/**
 * Input for Distance.
 * You have to option to use the shape radii
 * in the computation.
 *
 */
class DistanceInput {
    DistanceProxy proxyA = new DistanceProxy();
    DistanceProxy proxyB = new DistanceProxy();
    Transform transformA = new Transform();
    Transform transformB = new Transform();
    boolean useRadii;
}
