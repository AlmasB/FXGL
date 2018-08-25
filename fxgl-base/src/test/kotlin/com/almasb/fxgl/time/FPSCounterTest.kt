/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
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

        count += 16_666_666

        var fps = counter.update(count)

        assertThat(fps, `is`(60))

        counter.reset()

        count += 16_666_666
        fps = counter.update(count)

        assertThat(fps, `is`(not(60)))
    }
}