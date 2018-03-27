/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.algorithm

import javafx.geometry.Rectangle2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AASubdivisionTest {

    @Test
    fun `Test divide`() {
        for (i in 1..100) {
            val numSpaces = 5
            val minSize = 100

            val subspaces = AASubdivision.divide(Rectangle2D(0.0, 0.0, 1005.0, 105.0), numSpaces, minSize)

            assertThat(subspaces.size() <= numSpaces, `is`(true))

            subspaces.forEach { assertThat(it.width >= minSize && it.height >= minSize, `is`(true)) }
        }
    }
}