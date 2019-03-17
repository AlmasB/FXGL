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
class PerlinNoiseTest {

    @Test
    fun `Noise1D`() {
        for (y in 0..550) {

            val v1 = PerlinNoiseGenerator.noise1D(y.toDouble())

            assertThat(v1, allOf(greaterThanOrEqualTo(-0.0), lessThan(1.0)))
        }
    }
}