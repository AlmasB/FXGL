/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TimerTest {

    private lateinit var timer: Timer

    @BeforeEach
    fun `setUp`() {
        timer = Timer()
    }

    @Test
    fun `Run once`() {
        var count = 0

        timer.runOnceAfter({ count++ }, Duration.seconds(1.0))

        timer.update(1.0)
        assertThat(count, `is`(1))

        timer.update(1.0)
        assertThat(count, `is`(1))
    }

    @Test
    fun `Run at interval no limit`() {
        var count = 0

        timer.runAtInterval(Runnable { count++ }, Duration.seconds(1.0))

        timer.update(1.0)
        assertThat(count, `is`(1))

        timer.update(1.0)
        assertThat(count, `is`(2))

        timer.update(1.0)
        assertThat(count, `is`(3))
    }

    @Test
    fun `Run at interval with limit`() {
        var count = 0

        timer.runAtInterval(Runnable { count++ }, Duration.seconds(1.0), 2)

        timer.update(1.0)
        assertThat(count, `is`(1))

        timer.update(1.0)
        assertThat(count, `is`(2))

        timer.update(1.0)
        assertThat(count, `is`(2))
    }

    @Test
    fun `Clear`() {
        var count = 0

        timer.runOnceAfter(Runnable { count++ }, Duration.seconds(1.0))

        timer.clear()

        timer.update(1.0)
        assertThat(count, `is`(0))
    }

    @Test
    fun `Now value`() {
        timer.update(2.0)
        assertThat(timer.now, `is`(2.0))
    }
}