/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.test.RunWithFX
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class LoopRunnerTest {

    private lateinit var loop: LoopRunner

    @BeforeEach
    fun setUp() {
        loop = LoopRunner {  }
    }

    @Test
    fun `Start loop`() {
        assertFalse(loop.isStarted)

        loop.start()

        assertTrue(loop.isStarted)

        loop.stop()

        assertTrue(loop.isStarted)
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Update loop`() {
        var t = 0.0

        loop = LoopRunner { t += it }

        loop.start()

        Thread.sleep(1000)

        loop.pause()

        assertThat(loop.tpf, closeTo(0.016, 0.01))
        assertThat(loop.fps.toDouble(), closeTo(60.0, 1.0))

        assertThat(t, closeTo(1.0, 0.1))

        loop.resume()

        Thread.sleep(1000)

        loop.stop()

        assertThat(loop.tpf, closeTo(0.016, 0.01))
        assertThat(loop.fps.toDouble(), closeTo(60.0, 1.0))

        assertThat(t, closeTo(2.0, 0.1))

        // shouldn't change anything since loop is stopped
        Thread.sleep(300)

        assertThat(loop.tpf, closeTo(0.016, 0.01))
        assertThat(loop.fps.toDouble(), closeTo(60.0, 1.0))

        assertThat(t, closeTo(2.0, 0.1))
    }
}

//class FPSCounterTest {
//
//    private lateinit var counter: FPSCounter
//
//    @BeforeEach
//    fun `setUp`() {
//        counter = FPSCounter()
//    }
//
//    @Test
//    fun `Counters returns 60 fps if JavaFX timing is right`() {
//        var count = 1L
//
//        for (i in 1..100) {
//            count += 16_666_666
//            counter.update(count)
//        }
//
//        count += 16_666_666
//
//        var fps = counter.update(count)
//
//        assertThat(fps, `is`(60))
//
//        counter.reset()
//
//        count += 16_666_666
//        fps = counter.update(count)
//
//        assertThat(fps, `is`(not(60)))
//    }
//}