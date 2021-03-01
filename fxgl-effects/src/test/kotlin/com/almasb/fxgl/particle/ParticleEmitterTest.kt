/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.particle

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
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

    @Test
    fun `Built-in emitters return an emitter`() {
        assertThat(ParticleEmitters.newFireEmitter(), `is`(notNullValue()))
        assertThat(ParticleEmitters.newFireEmitter(100), `is`(notNullValue()))
        assertThat(ParticleEmitters.newExplosionEmitter(100), `is`(notNullValue()))
        assertThat(ParticleEmitters.newImplosionEmitter(), `is`(notNullValue()))
        assertThat(ParticleEmitters.newRainEmitter(100), `is`(notNullValue()))
        assertThat(ParticleEmitters.newSmokeEmitter(), `is`(notNullValue()))
        assertThat(ParticleEmitters.newSparkEmitter(), `is`(notNullValue()))
    }
}