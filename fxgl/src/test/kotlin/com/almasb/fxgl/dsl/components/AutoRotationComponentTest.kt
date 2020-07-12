/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AutoRotationComponentTest {

    @Test
    fun `Entity rotation is adjusted based on its movement velocity`() {
        val c = AutoRotationComponent()

        val e = Entity()
        e.addComponent(c)

        e.translate(-1.0, 1.0)
        c.onUpdate(0.016)

        assertThat(e.rotation, closeTo(135.0, 0.5))

        e.translate(1.0, -1.0)
        c.onUpdate(0.016)

        assertThat(e.rotation, closeTo(-45.0, 0.5))
    }

    @Test
    fun `Entity rotation smoothing picks the closest range angle`() {
        val c = AutoRotationComponent().withSmoothing()

        val e = Entity()
        e.addComponent(c)

        e.rotation = 170.0

        e.translate(-1.0, -1.0)
        c.onUpdate(0.016)

        assertThat(e.rotation, greaterThan(170.0))
    }
}