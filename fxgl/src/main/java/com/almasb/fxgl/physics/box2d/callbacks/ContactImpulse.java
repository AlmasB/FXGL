/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.common.JBoxSettings;

/**
 * Contact impulses for reporting. Impulses are used instead of forces because sub-step forces may
 * approach infinity for rigid body collisions. These match up one-to-one with the contact points in
 * b2Manifold.
 *
 * @author Daniel Murphy
 */
public class ContactImpulse {
    public float[] normalImpulses = new float[JBoxSettings.maxManifoldPoints];
    public float[] tangentImpulses = new float[JBoxSettings.maxManifoldPoints];
    public int count;
}
