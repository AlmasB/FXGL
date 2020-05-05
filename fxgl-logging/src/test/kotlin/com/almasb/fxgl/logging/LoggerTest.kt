/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.logging

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LoggerTest {

    @Test
    fun `Configuring logger more than once does not throw`() {
        Logger.configure(LoggerConfig())

        assertDoesNotThrow {
            Logger.configure(LoggerConfig())
        }
    }

    @Test
    fun `Closing logger more than once does not throw`() {
        Logger.close()

        assertDoesNotThrow {
            Logger.close()
        }
    }
}