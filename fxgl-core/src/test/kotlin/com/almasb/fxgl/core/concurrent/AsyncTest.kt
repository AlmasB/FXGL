/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent

import com.almasb.fxgl.test.RunWithFX
import javafx.application.Platform
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class AsyncTest {

    @Test
    fun `Async runs in a different thread`() {
        assertTimeout(Duration.ofSeconds(1)) {
            var count = 0
            val threadID1 = Thread.currentThread().id
            var threadID2 = -333L

            Async.start {
                count = 1

                threadID2 = Thread.currentThread().id
            }.await()

            assertThat(count, `is`(1))
            assertFalse(threadID1 == threadID2)

            count = Async.start<Int> { 99 }.await()

            assertThat(count, `is`(99))
        }
    }

    @Test
    fun `Async FX runs in a FX thread`() {
        assertTimeout(Duration.ofSeconds(1)) {
            var count = 0
            val threadID1 = Thread.currentThread().id
            var threadID2 = -333L

            Async.startFX {
                count = 1

                threadID2 = Thread.currentThread().id

                assertTrue(Platform.isFxApplicationThread())
            }.await()

            assertThat(count, `is`(1))
            assertFalse(threadID1 == threadID2)

            count = Async.startFX<Int> { 99 }.await()

            assertThat(count, `is`(99))
        }
    }
}