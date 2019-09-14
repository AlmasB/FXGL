/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.particle

import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ParticleSystemTest {

    @Test
    fun `Particles are drawn to pane`() {
        val system = ParticleSystem()

        val emitter = ParticleEmitter()
        emitter.emissionRate = 1.0
        emitter.numParticles = 15
        emitter.maxEmissions = 3
        emitter.setExpireFunction { Duration.seconds(2.0) }

        system.addParticleEmitter(emitter, 100.0, 100.0)

        assertThat(system.pane.children.size, `is`(0))

        system.onUpdate(1.0)

        assertThat(system.pane.children.size, `is`(15))

        system.onUpdate(0.5)

        assertThat(system.pane.children.size, `is`(30))

        system.onUpdate(0.5)

        // the first batch (15) should have died, but at each frame we spawn 15
        assertThat(system.pane.children.size, `is`(30))

        system.onUpdate(2.0)

        // we set max emissions to 3, so all should have died by now
        assertThat(system.pane.children.size, `is`(0))

        system.removeParticleEmitter(emitter)
    }
}