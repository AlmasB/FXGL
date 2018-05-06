/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TimerActionTest {

    @Test
    fun `Timer action does not run in first onUpdate frame if time not passed`() {
        var count = 0

        val action = TimerAction(Duration.millis(150.0), Runnable { count++ }, 1)
        action.update(0.016)

        assertThat(count, `is`(0))
    }

    @Test
    fun `Timer action runs when time passed`() {
        var count = 0

        val action = TimerAction(Duration.millis(150.0), Runnable { count++ }, 1)
        action.update(0.15)

        assertThat(count, `is`(1))
    }

    @Test
    fun `Timer action with type once runs once when time passed`() {
        var count = 0

        val action = TimerAction(Duration.millis(150.0), Runnable { count++ }, 1)
        action.update(0.15)

        action.update(0.15)

        assertThat(count, `is`(1))
    }

    @Test
    fun `Timer action with type once is expired after run`() {
        var count = 0

        val action = TimerAction(Duration.millis(150.0), Runnable { count++ }, 1)
        action.update(0.15)

        assertTrue(action.isExpired)
    }

    @Test
    fun `Timer action with type indefinite does not expire after run`() {
        var count = 0

        val action = TimerAction(Duration.millis(150.0), Runnable { count++ })
        action.update(0.15)

        assertFalse(action.isExpired)
    }

    @Test
    fun `Timer action with limit 0 does not run`() {
        var count = 0

        val action = TimerAction(Duration.millis(150.0), Runnable { count++ }, 0)
        assertTrue(action.isExpired)

        action.update(0.15)
        assertThat(count, `is`(0))
    }

    @Test
    fun `Timer action does not run when paused`() {
        var count = 0

        val action = TimerAction(Duration.millis(150.0), Runnable { count++ }, 1)
        action.pause()

        assertTrue(action.isPaused)

        action.update(0.15)
        assertThat(count, `is`(0))
    }

    @Test
    fun `Timer action runs when resumed`() {
        var count = 0

        val action = TimerAction(Duration.millis(150.0), Runnable { count++ }, 1)
        action.pause()
        action.resume()

        assertFalse(action.isPaused)

        action.update(0.15)
        assertThat(count, `is`(1))
    }
}