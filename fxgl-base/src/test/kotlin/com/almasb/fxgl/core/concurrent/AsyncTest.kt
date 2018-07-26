/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent

import com.almasb.fxgl.app.FXGLMock
import javafx.application.Platform
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AsyncTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

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