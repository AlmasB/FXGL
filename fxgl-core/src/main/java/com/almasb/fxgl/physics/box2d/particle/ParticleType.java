/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.particle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public enum ParticleType {
    WATER(ParticleTypeInternal.b2_waterParticle),
    WALL(ParticleTypeInternal.b2_wallParticle),
    SPRING(ParticleTypeInternal.b2_springParticle),
    ELASTIC(ParticleTypeInternal.b2_elasticParticle),
    VISCOUS(ParticleTypeInternal.b2_viscousParticle),
    POWDER(ParticleTypeInternal.b2_powderParticle),
    TENSILE(ParticleTypeInternal.b2_tensileParticle);

    final int bit;

    ParticleType(int bit) {
        this.bit = bit;
    }
}
