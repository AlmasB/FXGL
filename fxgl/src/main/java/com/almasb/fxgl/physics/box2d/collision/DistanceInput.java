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
public class DistanceInput {
    public DistanceProxy proxyA = new DistanceProxy();
    public DistanceProxy proxyB = new DistanceProxy();
    public Transform transformA = new Transform();
    public Transform transformB = new Transform();
    public boolean useRadii;
}
