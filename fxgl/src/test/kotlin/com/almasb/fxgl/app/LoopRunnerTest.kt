/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.app

import com.almasb.fxgl.test.RunWithFX
import javafx.application.Platform
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class LoopRunnerTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Update loop`() {
        var t = 0.0

        listOf(
                // run with a given ticks per second (via scheduled service tick)
                LoopRunner(60) { t += it },

                // run with display refresh rate (via JavaFX pulse tick)
                LoopRunner { t += it }
        ).forEach { loop ->
            t = 0.0

            loop.start()

            Thread.sleep(1000)

            loop.pause()

            assertThat(loop.tpf, closeTo(0.016, 0.09))
            assertThat(loop.fps.toDouble(), closeTo(60.0, 5.0))

            assertThat(t, closeTo(1.0, 0.2))

            loop.resume()

            Thread.sleep(1000)

            loop.stop()

            assertThat(loop.tpf, closeTo(0.016, 0.09))
            assertThat(loop.fps.toDouble(), closeTo(60.0, 5.0))

            assertThat(t, closeTo(2.0, 0.4))

            // shouldn't change anything since loop is stopped
            Thread.sleep(300)

            assertThat(loop.tpf, closeTo(0.016, 0.09))
            assertThat(loop.fps.toDouble(), closeTo(60.0, 5.0))

            assertThat(t, closeTo(2.0, 0.4))
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `LoopRunner runs ticks on JavaFX thread`() {
        var count1 = 0.0
        var count2 = 0.0

        listOf(
                // run with a given ticks per second (via scheduled service tick)
                LoopRunner(60) {
                    assertTrue(Platform.isFxApplicationThread())
                    count1 += it
                },

                // run with display refresh rate (via JavaFX pulse tick)
                LoopRunner {
                    assertTrue(Platform.isFxApplicationThread())
                    count2 += it
                }
        ).forEach {
            it.start()

            Thread.sleep(1000)

            it.stop()
        }

        assertThat(count1, greaterThan(0.0))
        assertThat(count2, greaterThan(0.0))
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `LoopRunner resets ticks after pause`() {
        var count1 = 0.0
        var count2 = 0.0

        listOf(
                // run with a given ticks per second (via scheduled service tick)
                LoopRunner(60) {

                    count1 += it
                },

                // run with display refresh rate (via JavaFX pulse tick)
                LoopRunner {

                    count2 += it
                }
        ).forEach {
            it.start()

            // 16.6 per frame, so 10 frames
            Thread.sleep(166)

            it.pause()

            // sleep for 150 frames = 2.5 sec
            Thread.sleep(166 * 15)

            it.resume()

            // 16.6 per frame, so 10 frames
            Thread.sleep(166)

            it.stop()
        }

        // in total we should have computed 20 frames, ~20 * 0.017 = ~0.34

        assertThat(count1, greaterThan(0.0))
        assertThat(count1, lessThan(0.75))

        assertThat(count2, greaterThan(0.0))
        assertThat(count2, lessThan(0.75))
    }
}