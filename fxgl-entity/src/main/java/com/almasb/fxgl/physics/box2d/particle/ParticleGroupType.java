/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.particle;

public class ParticleGroupType {
    /**
     * resists penetration
     */
    public static final int b2_solidParticleGroup = 1 << 0;
    /**
     * keeps its shape
     */
    public static final int b2_rigidParticleGroup = 1 << 1;
}
