/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SimplexNoiseTest {

    @Test
    fun `Noises`() {
        for (y in 0..250) {
            for (x in 0..250) {
                val v1 = SimplexNoise.noise2D(x.toDouble(), y.toDouble())

                val v2 = SimplexNoise.noise3D(x.toDouble(), y.toDouble(), x*y.toDouble())

                assertThat(v1, allOf(greaterThan(-1.0), lessThan(1.0)))
                assertThat(v2, allOf(greaterThan(-1.0), lessThan(1.0)))
            }
        }
    }
}