/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.particle

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ParticleEmitterTest {

    @Test
    fun `Emit particles`() {
        val emitter = ParticleEmitter()
        emitter.maxEmissions = 1
        emitter.numParticles = 22

        val particles = emitter.emit(10.0, 10.0)


        assertThat(particles.size(), `is`(22))
    }
}