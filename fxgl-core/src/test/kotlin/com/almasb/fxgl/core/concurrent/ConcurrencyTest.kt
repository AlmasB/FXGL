/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent

import com.almasb.fxgl.test.RunWithFX
import javafx.application.Platform
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.IllegalStateException
import java.time.Duration.ofSeconds
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

        @AfterAll
        @JvmStatic
        fun `tearDown`() {
            executor.shutdownNow()
        }
    }

    @BeforeEach
    fun `init`() {
        executor = FXGLExecutor()
    }

    @Test
    fun `Test that execute runs in a different thread`() {
        assertTimeout(ofSeconds(2)) {
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
        assertTimeout(ofSeconds(2)) {
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
            assertTrue(diff > 800 && diff < 1200)
        }
    }

    @Test
    fun `Async runs in a different thread`() {
        assertTimeout(ofSeconds(1)) {
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
        assertTimeout(ofSeconds(1)) {
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

    /* IO Task tests */

    @Test
    fun `IOTask name`() {
        val task = IOTask.ofVoid("someName") { }

        assertThat(task.name, `is`("someName"))
        assertThat(IOTask.ofVoid {  }.name, `is`("NoName"))
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
                .onFailure {
                    exceptionMessage = it.message.orEmpty()
                }

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
    fun `IOTask Async runs in a different thread`() {
        assertTimeout(ofSeconds(1)) {
            var count = 0
            var threadID = -333L

            val task = IOTask.of {
                count++

                threadID = Thread.currentThread().id

                "MyString"
            }

            var result = ""

            val executor = java.util.concurrent.Executor { Thread(it).run() }

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
    fun `IOTask Async FX runs in a JavaFX UI thread`() {
        assertTimeout(ofSeconds(1)) {
            var count = 0
            var threadID = -333L

            val task = IOTask.of {
                count++

                threadID = Thread.currentThread().id

                "MyString"
            }

            var result = ""

            val executor = java.util.concurrent.Executor { Thread(it).run() }

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