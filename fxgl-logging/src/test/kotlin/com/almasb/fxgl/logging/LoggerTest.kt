/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.logging

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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

    @Test
    fun `Logger levels`() {
        val log = Logger.get("test")

        val output1 = object : LoggerOutput {
            val testMessages = arrayListOf<String>()

            override fun append(message: String) {
                testMessages += message
            }

            override fun close() {
            }
        }

        val output2 = object : LoggerOutput {
            val testMessages = arrayListOf<String>()

            override fun append(message: String) {
                testMessages += message
            }

            override fun close() {
            }
        }

        Logger.addOutput(output1, LoggerLevel.INFO)
        Logger.addOutput(output2, LoggerLevel.FATAL)

        log.warning("hello")

        assertThat(output1.testMessages.size, `is`(1))
        assertThat(output2.testMessages.size, `is`(0))

        log.info("world")

        assertThat(output1.testMessages.size, `is`(2))
        assertThat(output2.testMessages.size, `is`(0))

        log.fatal("bye!")

        assertThat(output1.testMessages.size, `is`(3))
        assertThat(output2.testMessages.size, `is`(1))

        Logger.removeOutput(output1, LoggerLevel.INFO)
        Logger.removeOutput(output2, LoggerLevel.FATAL)
    }

    @Test
    fun `Default message formatting`() {
        val formatter = DefaultMessageFormatter()
        var message = formatter.makeMessage("DateTime", "ThreadName", "LoggerLevel", "SuperLongLoggerNameToBeTruncated", "LoggerMessage")

        assertThat(message, `is`("DateTime [ThreadName               ] Logge SuperLongLoggerNameT - LoggerMessage"))

        message = formatter.makeMessage("DateTime", "ThreadName", "LoggerLevel", "ShortName", "LoggerMessage")

        assertThat(message, `is`("DateTime [ThreadName               ] Logge ShortName            - LoggerMessage"))
    }
}