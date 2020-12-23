/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PauseMenuBGGenTest {

    @Test
    fun `Gen`() {
        val result = PauseMenuBGGen.generate()

        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(Matchers.greaterThan(0)))
    }
}