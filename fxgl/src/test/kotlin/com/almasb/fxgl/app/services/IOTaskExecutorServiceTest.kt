/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.app.MockDialogService
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.net.NetService
import com.almasb.fxgl.test.InjectInTest
import com.almasb.fxgl.test.RunWithFX
import com.almasb.fxgl.ui.DialogBox
import com.almasb.fxgl.ui.DialogService
import javafx.application.Platform
import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.control.Button
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
import java.lang.RuntimeException
import java.lang.invoke.MethodHandles
import java.time.Duration.ofSeconds
import java.util.concurrent.CountDownLatch
import java.util.function.Consumer
import java.util.function.Predicate

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class IOTaskExecutorServiceTest {

    private lateinit var service: IOTaskExecutorService

    @BeforeEach
    fun setUp() {
        service = IOTaskExecutorService()

        val lookup = MethodHandles.lookup()

        InjectInTest.inject(lookup, service, "dialogService", MockDialogService)
    }

    @Test
    fun `Run on same thread`() {
        var threadID = -33L

        val task = IOTask.of {
            threadID = Thread.currentThread().id

            "MyString"
        }

        val result = service.run(task)

        assertThat(result, `is`("MyString"))
        assertThat(threadID, `is`(Thread.currentThread().id))
    }

    @Test
    fun `IOTask runs in a different thread`() {
        assertTimeout(ofSeconds(1)) {
            var count = 0
            var threadID = -333L

            val task = IOTask.of {
                count++

                threadID = Thread.currentThread().id

                "MyString"
            }

            var result = ""

            val latch = CountDownLatch(1)

            task.onSuccess {
                count++

                assertThat(threadID, `is`(not(-333L)))
                assertThat(threadID, `is`(Thread.currentThread().id))

                result = it

                latch.countDown()
            }

            service.runAsync(task)

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

            val latch = CountDownLatch(1)

            task.onSuccess {
                count++

                assertThat(threadID, `is`(not(-333L)))
                assertThat(threadID, `is`(not(Thread.currentThread().id)))
                assertTrue(Platform.isFxApplicationThread())

                result = it

                latch.countDown()
            }

            service.runAsyncFX(task)

            latch.await()

            assertThat(count, `is`(2))
            assertThat(result, `is`("MyString"))
        }
    }

    @Test
    fun `IOTask Async FX with dialog success`() {
        assertTimeout(ofSeconds(1)) {
            val task = IOTask.of {
                "MyString"
            }

            var result = ""

            val latch = CountDownLatch(1)

            task.onSuccess {
                result = it

                latch.countDown()
            }

            service.runAsyncFXWithDialog(task, "test")

            latch.await()

            assertThat(result, `is`("MyString"))
        }
    }

    @Test
    fun `IOTask Async FX with dialog fail`() {
        assertTimeout(ofSeconds(1)) {
            val task = IOTask.of {
                throw RuntimeException("err")
            }

            var result: Throwable = RuntimeException("fine")

            val latch = CountDownLatch(1)

            task.onFailure {
                result = it

                latch.countDown()
            }

            service.runAsyncFXWithDialog(task, "test")

            latch.await()

            assertThat(result.message, `is`("err"))
        }
    }
}