/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.animation

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InterpolatorsTest {

    @ParameterizedTest
    @EnumSource(Interpolators::class)
    fun `Interpolators start and end with 0 and 1`(interpolators: Interpolators) {
        val i1 = interpolators.EASE_IN()
        val i2 = interpolators.EASE_OUT()
        val i3 = interpolators.EASE_IN_OUT()

        assertThat(i1.interpolate(0.0, 1.0, 0.0), `is`(0.0))
        assertThat(i2.interpolate(0.0, 1.0, 0.0), `is`(0.0))
        assertThat(i3.interpolate(0.0, 1.0, 0.0), `is`(0.0))

        // just check that these do not crash
        i1.interpolate(0.0, 1.0, 0.5)
        i2.interpolate(0.0, 1.0, 0.5)
        i3.interpolate(0.0, 1.0, 0.5)

        i1.interpolate(0.0, 1.0, 0.25)
        i2.interpolate(0.0, 1.0, 0.25)
        i3.interpolate(0.0, 1.0, 0.25)

        i1.interpolate(0.0, 1.0, 0.75)
        i2.interpolate(0.0, 1.0, 0.75)
        i3.interpolate(0.0, 1.0, 0.75)

        i1.interpolate(0.0, 1.0, 0.15)
        i2.interpolate(0.0, 1.0, 0.15)
        i3.interpolate(0.0, 1.0, 0.15)

        i1.interpolate(0.0, 1.0, 0.85)
        i2.interpolate(0.0, 1.0, 0.85)
        i3.interpolate(0.0, 1.0, 0.85)

        assertThat(i1.interpolate(0.0, 1.0, 1.0), `is`(1.0))
        assertThat(i2.interpolate(0.0, 1.0, 1.0), `is`(1.0))
        assertThat(i3.interpolate(0.0, 1.0, 1.0), `is`(1.0))
    }
}