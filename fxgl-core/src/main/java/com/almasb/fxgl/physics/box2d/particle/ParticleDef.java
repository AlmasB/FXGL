/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.particle;

import com.almasb.fxgl.core.math.Vec2;

public class ParticleDef {
    /**
     * Specifies the type of particle. A particle may be more than one type. Multiple types are
     * chained by logical sums, for example: pd.typeFlags = ParticleType.b2_elasticParticle |
     * ParticleType.b2_viscousParticle.
     */
    private int typeFlags = ParticleTypeInternal.b2_waterParticle;  // 0

    public int getTypeFlags() {
        return typeFlags;
    }

    public void setTypeFlags(int typeFlags) {
        this.typeFlags = typeFlags;
    }

    /**
     * The world position of the particle.
     */
    public final Vec2 position = new Vec2();

    /**
     * The linear velocity of the particle in world co-ordinates.
     */
    public final Vec2 velocity = new Vec2();

    /**
     * The color of the particle.
     */
    public ParticleColor color;

    /**
     * Use this to store application-specific body data.
     */
    private Object userData;

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }
}
