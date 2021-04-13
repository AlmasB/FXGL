/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.core.concurrent

import com.almasb.fxgl.test.RunWithFX
import javafx.application.Platform
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTimeoutPreemptively
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration.ofSeconds
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class ConcurrencyTest {

    companion object {
        private lateinit var executor: Executor

        @BeforeAll
        @JvmStatic
        fun `init`() {
            executor = Async
        }
    }

    @Test
    fun `Test that execute runs in a different thread`() {
        assertTimeoutPreemptively(ofSeconds(2)) {
            val id1 = Thread.currentThread().id
            var id2 = -1L

            val latch = CountDownLatch(1)

            executor.execute {
                id2 = Thread.currentThread().id
                latch.countDown()
            }

            latch.await()

            assertThat(id2, `is`(not(-1L)))
            assertThat(id2, `is`(not(id1)))
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Test schedule runs after given delay`() {
        assertTimeoutPreemptively(ofSeconds(2)) {
            val now = System.currentTimeMillis()
            var diff = -1L

            val latch = CountDownLatch(1)

            executor.schedule({
                diff = System.currentTimeMillis() - now
                latch.countDown()
            }, Duration.seconds(1.0))

            latch.await()

            assertThat(diff, `is`(not(-1L)))
            // allow +-200ms error
            assertTrue(diff in 801..1199)
        }
    }

    @Test
    fun `Async runs in a different thread`() {
        assertTimeoutPreemptively(ofSeconds(1)) {
            var count = 0
            val threadID1 = Thread.currentThread().id
            var threadID2 = -333L

            executor.startAsync {
                count = 1

                threadID2 = Thread.currentThread().id
            }.await()

            assertThat(count, `is`(1))
            assertFalse(threadID1 == threadID2)

            count = executor.startAsync<Int> { 99 }.await()

            assertThat(count, `is`(99))
        }
    }

    @Test
    fun `Exception is rethrown to FX thread if async fails`() {
        val latch = CountDownLatch(1)

        executor.startAsyncFX {
            Thread.setDefaultUncaughtExceptionHandler { _, e ->
                assertThat(e.message, `is`("Test"))
                latch.countDown()
            }
        }.await()

        assertTimeoutPreemptively(ofSeconds(1)) {
            executor.startAsync {
                throw RuntimeException("Test")
            }.await()

            latch.await()
        }
    }

    @Test
    fun `Async FX runs in a FX thread`() {
        assertTimeoutPreemptively(ofSeconds(1)) {
            var count = 0
            val threadID1 = Thread.currentThread().id
            var threadID2 = -333L

            executor.startAsyncFX {
                count = 1

                threadID2 = Thread.currentThread().id

                assertTrue(Platform.isFxApplicationThread())
            }.await()

            assertThat(count, `is`(1))
            assertFalse(threadID1 == threadID2)

            count = executor.startAsyncFX<Int> { 99 }.await()

            assertThat(count, `is`(99))
        }
    }

    @Test
    fun `Async FX runs immediately if started in a FX thread`() {
        assertTimeoutPreemptively(ofSeconds(1)) {
            val latch = CountDownLatch(1)

            Platform.runLater {

                assertTrue(Platform.isFxApplicationThread())

                val value = executor.startAsyncFX<Int> { 99 }.await()

                assertThat(value, `is`(99))

                latch.countDown()
            }

            latch.await()
        }
    }

    /* IO Task tests */

    @Test
    fun `IOTask name`() {
        var count = 0

        val task = IOTask.ofVoid("someName") { count = 1 }

        assertThat(task.name, `is`("someName"))
        assertThat(IOTask.ofVoid {  }.name, `is`("NoName"))

        task.run()

        assertThat(count, `is`(1))
    }

    @Test
    fun `IOTask correctly handles successful run`() {
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

        assertThat(SomeIOTask().run(), `is`("RESULT"))
    }

    @Test
    fun `IOTask correctly handles failing run`() {
        var exceptionMessage = "NoMessage"

        val task = SomeFailingIOTask()

        assertFalse(task.hasFailAction())

        task.onFailure {
            exceptionMessage = it.message.orEmpty()
        }

        assertTrue(task.hasFailAction())

        assertNull(task.run())

        assertThat(exceptionMessage, `is`("EXCEPTION_MESSAGE"))
    }

    @Test
    fun `IOTask combine`() {
        val task = SomeIOTask().then { IOTask.of { it.length } }
        assertThat(task.run(), `is`(6))

        val task2 = task.thenWrap { it * 2 }.thenWrap { it.toString() }
        assertThat(task2.run(), `is`("12"))
    }

    @Test
    fun `IOTask to JavaFX task`() {
        val task1 = SomeIOTask().toJavaFXTask()
        val task2 = SomeFailingIOTask().toJavaFXTask()

        assertDoesNotThrow {
            task1.run()
            task2.run()
        }
    }

    @Test
    fun `IOTask to JavaFX can be cancelled before run then onCancel is called during run`() {

        assertTimeoutPreemptively(ofSeconds(1)) {
            var cancelText = ""

            val latch = CountDownLatch(1)

            val task = SomeIOTask().onCancel {
                cancelText = "cancelled"

                latch.countDown()
            }

            task.cancel()

            val fxTask = task.toJavaFXTask()
            fxTask.run()

            latch.await()

            assertThat(cancelText, `is`("cancelled"))
        }
    }

    @Test
    fun `IOTask can be cancelled before run then onCancel is called during run`() {
        var cancelText = ""

        val task = SomeIOTask().onCancel { cancelText = "cancelled" }

        assertFalse(task.isCancelled)

        task.cancel()

        assertTrue(task.isCancelled)

        val result = task.run()

        assertNull(result)
        assertThat(cancelText, `is`("cancelled"))
    }

    @Test
    fun `IOTask can be cancelled during run`() {
        var count = 0
        var cancelText = ""

        val task = object : IOTask<String>() {
            override fun onExecute(): String {
                count++
                cancel()
                count++

                if (isCancelled)
                    throwCancelException()

                return "RESULT"
            }
        }.onCancel { cancelText = "cancelled" }

        val result = task.run()

        assertNull(result)
        assertThat(cancelText, `is`("cancelled"))
        assertThat(count, `is`(2))
    }

    @Test
    fun `Cancelling IOTask after run is noop`() {
        var cancelText = ""

        val task = SomeIOTask().onCancel { cancelText = "cancelled" }

        val result = task.run()

        assertThat(result, `is`("RESULT"))
        assertThat(cancelText, `is`(""))

        task.cancel()

        assertThat(result, `is`("RESULT"))
        assertThat(cancelText, `is`(""))
    }

    class SomeIOTask : IOTask<String>() {
        override fun onExecute(): String {
            return "RESULT"
        }
    }

    class SomeFailingIOTask : IOTask<String>() {
        override fun onExecute(): String {
            throw IllegalStateException("EXCEPTION_MESSAGE")
        }
    }
}