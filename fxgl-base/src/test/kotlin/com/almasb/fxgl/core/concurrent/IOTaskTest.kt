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
import org.junit.jupiter.api.Assertions.assertTimeout
import org.junit.jupiter.api.Assertions.assertTrue
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
class IOTaskTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @Test
    fun `Task run`() {
        var result = ""
        var exceptionMessage = "NoMessage"

        val task = IOTask.of { "MyString" }
        task.onSuccess {
            result = it
        }
        task.onFailure {
            exceptionMessage = it.message.orEmpty()
        }

        assertThat(task.run(), `is`("MyString"))
        assertThat(result, `is`("MyString"))
        assertThat(exceptionMessage, `is`("NoMessage"))
    }

    @Test
    fun `Async runs in a different thread`() {
        assertTimeout(Duration.ofSeconds(1)) {
            var count = 0
            var threadID = -333L

            val task = IOTask.of {
                count++

                threadID = Thread.currentThread().id

                "MyString"
            }

            var result = ""

            val executor = Executor { Thread(it).run() }

            val latch = CountDownLatch(1)

            task.onSuccess {
                count++

                assertThat(threadID, `is`(not(-333L)))
                assertThat(threadID, `is`(Thread.currentThread().id))

                result = it

                latch.countDown()
            }

            task.runAsync(executor)

            latch.await()

            assertThat(count, `is`(2))
            assertThat(result, `is`("MyString"))
        }
    }

    @Test
    fun `Async FX runs in a JavaFX UI thread`() {
        assertTimeout(Duration.ofSeconds(1)) {
            var count = 0
            var threadID = -333L

            val task = IOTask.of {
                count++

                threadID = Thread.currentThread().id

                "MyString"
            }

            var result = ""

            val executor = Executor { Thread(it).run() }

            val latch = CountDownLatch(1)

            task.onSuccess {
                count++

                assertThat(threadID, `is`(not(-333L)))
                assertThat(threadID, `is`(not(Thread.currentThread().id)))
                assertTrue(Platform.isFxApplicationThread())

                result = it

                latch.countDown()
            }

            task.runAsyncFX(executor)

            latch.await()

            assertThat(count, `is`(2))
            assertThat(result, `is`("MyString"))
        }
    }
}