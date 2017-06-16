/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.dynamics.World;
import com.almasb.fxgl.physics.box2d.particle.ParticleGroup;

public interface ParticleDestructionListener {

    /**
     * Called when any particle group is about to be destroyed.
     */
    void onDestroy(ParticleGroup group);

    /**
     * Called when a particle is about to be destroyed. The index can be used in conjunction with
     * {@link World#getParticleUserDataBuffer} to determine which particle has been destroyed.
     *
     * @param index the index to particle user data buffer
     */
    void onDestroy(int index);
}
