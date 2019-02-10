/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import com.almasb.fxgl.core.serialization.Bundle
import javafx.util.Duration
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OfflineTimerTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Test offline timer`() {
        val bundle = Bundle("testbundle")
        val timer = OfflineTimer("test", bundle)

        // no previous data, so any duration will return true
        assertTrue(timer.elapsed(Duration.seconds(1.0)))

        timer.capture()

        assertFalse(timer.elapsed(Duration.millis(500.0)))

        Thread.sleep(1000)

        assertTrue(timer.elapsed(Duration.millis(500.0)))
    }
}