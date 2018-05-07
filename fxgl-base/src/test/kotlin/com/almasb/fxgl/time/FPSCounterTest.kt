/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FPSCounterTest {

    private lateinit var counter: FPSCounter

    @BeforeEach
    fun `setUp`() {
        counter = FPSCounter()
    }

    @Test
    fun `Counters returns 60 fps if JavaFX timing is right`() {
        var count = 1L

        for (i in 1..100) {
            count += 16_666_666
            counter.update(count)
        }

        val fps = counter.update(count + 16_666_666)

        assertThat(fps, `is`(60))
    }
}