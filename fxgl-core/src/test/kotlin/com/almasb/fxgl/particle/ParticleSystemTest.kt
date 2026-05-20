/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.particle

import com.almasb.fxgl.core.pool.Pools
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
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

    /**
     * Regression test for issue #1417.
     *
     * Before the fix, ParticleSystem.onUpdate called Pools.free(p) where `p`
     * was the emitter's Point2D position (shadowed by the destructured lambda
     * parameter), not the expired Particle. Because Pools.free is a no-op for
     * unregistered types, expired Particles were never returned to the pool.
     *
     * This test asserts that, after a full spawn/expire cycle, a Particle pool
     * has been registered in Pools.typePools — confirming that Pools.free was
     * actually invoked with a Particle instance.
     */
    @Test
    fun `Expired particles are returned to the pool`() {
        val system = ParticleSystem()

        val emitter = ParticleEmitter()
        emitter.emissionRate = 1.0
        emitter.numParticles = 50
        emitter.maxEmissions = 5
        emitter.setExpireFunction { Duration.seconds(0.5) }

        system.addParticleEmitter(emitter, 0.0, 0.0)

        // Drive the system long enough that all 5 * 50 = 250 particles spawn and die
        repeat(10) { system.onUpdate(0.5) }

        // After the run, no live particles should remain in the scene
        assertThat(system.pane.children.size, `is`(0))

        // Reflectively inspect Pools.typePools to confirm a Particle pool exists.
        // Pre-fix, the pool stayed empty because Pools.free(Point2D) was a no-op.
        val typePoolsField = Pools::class.java.getDeclaredField("typePools")
        typePoolsField.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val typePools = typePoolsField.get(null) as Map<Class<*>, *>

        val particlePool = typePools[Particle::class.java]
        assertNotNull(particlePool, "Particle pool should exist after free()")

        system.removeParticleEmitter(emitter)
    }
}
