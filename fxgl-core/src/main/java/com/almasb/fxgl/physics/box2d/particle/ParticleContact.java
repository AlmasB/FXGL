/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.particle;

import com.almasb.fxgl.core.math.Vec2;

public class ParticleContact {
    /**
     * Indices of the respective particles making contact.
     */
    public int indexA, indexB;
    /**
     * The logical sum of the particle behaviors that have been set.
     */
    public int flags;
    /**
     * Weight of the contact. A value between 0.0f and 1.0f.
     */
    public float weight;
    /**
     * The normalized direction from A to B.
     */
    public final Vec2 normal = new Vec2();
}
