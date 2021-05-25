/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

        // the ++ and -- below indicate the direction angle should move for smoothing
        // the main issue here is to smooth from -180 to 180 we need to avoid rotating clockwise
        // and vice versa

        // from top-right to bottom-right, -45.0 ++

        e.rotation = -45.0

        e.translate(1.0, 1.0)
        c.onUpdate(0.016)

        assertThat(e.rotation, `is`(-35.976232774459554))

        // from top-left to bottom-left, -135.0 --

        e.rotation = -135.0

        e.translate(-1.0, 1.0)
        c.onUpdate(0.016)

        assertThat(e.rotation, `is`(-144.02376722554044))

        // from bottom-left to top-left, 135.0 ++

        e.rotation = 135.0

        e.translate(-1.0, -1.0)
        c.onUpdate(0.016)

        assertThat(e.rotation, `is`(144.02376722554044))

        // from bottom-left to bottom-right, 135.0 --

        e.rotation = 135.0

        e.translate(1.0, 1.0)
        c.onUpdate(0.016)

        assertThat(e.rotation, `is`(126.02376722554044))
    }
}