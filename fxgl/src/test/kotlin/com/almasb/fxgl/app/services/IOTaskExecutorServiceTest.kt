/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

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
import java.time.Duration.ofSeconds
import java.util.concurrent.CountDownLatch

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IOTaskExecutorServiceTest {

    // TODO:
    // first we runAsync then we runAsyncFX
//            if (count == 0) {
//                assertFalse(Platform.isFxApplicationThread())
//            } else if (count == 1) {
//                assertTrue(Platform.isFxApplicationThread())
//            }
//
//            count++
//
//            latch.countDown()
    //
//    @Test
//    fun `IOTask Async runs in a different thread`() {
//        assertTimeout(ofSeconds(1)) {
//            var count = 0
//            var threadID = -333L
//
//            val task = IOTask.of {
//                count++
//
//                threadID = Thread.currentThread().id
//
//                "MyString"
//            }
//
//            var result = ""
//
//            val executor = java.util.concurrent.Executor { Thread(it).run() }
//
//            val latch = CountDownLatch(1)
//
//            task.onSuccess {
//                count++
//
//                assertThat(threadID, `is`(not(-333L)))
//                assertThat(threadID, `is`(Thread.currentThread().id))
//
//                result = it
//
//                latch.countDown()
//            }
//
//            task.runAsync(executor)
//
//            latch.await()
//
//            assertThat(count, `is`(2))
//            assertThat(result, `is`("MyString"))
//        }
//    }
//
//    @Test
//    fun `IOTask Async FX runs in a JavaFX UI thread`() {
//        assertTimeout(ofSeconds(1)) {
//            var count = 0
//            var threadID = -333L
//
//            val task = IOTask.of {
//                count++
//
//                threadID = Thread.currentThread().id
//
//                "MyString"
//            }
//
//            var result = ""
//
//            val executor = java.util.concurrent.Executor { Thread(it).run() }
//
//            val latch = CountDownLatch(1)
//
//            task.onSuccess {
//                count++
//
//                assertThat(threadID, `is`(not(-333L)))
//                assertThat(threadID, `is`(not(Thread.currentThread().id)))
//                assertTrue(Platform.isFxApplicationThread())
//
//                result = it
//
//                latch.countDown()
//            }
//
//            task.runAsyncFX(executor)
//
//            latch.await()
//
//            assertThat(count, `is`(2))
//            assertThat(result, `is`("MyString"))
//        }
//    }
//
//    @Test
//    fun `IOTask Async FX with dialog`() {
//        assertTimeout(ofSeconds(1)) {
//            var count = 0
//
//            val task = IOTask.of {
//                "MyString"
//            }
//
//            var result = ""
//
//            val latch = CountDownLatch(1)
//
//            task.onSuccess {
//                result = it
//
//                latch.countDown()
//            }
//
//            task.runAsyncFXWithDialog(object : IOTask.UIDialogHandler {
//                override fun dismiss() {
//                    count++
//                }
//
//                override fun show() {
//                    count++
//                }
//            })
//
//            latch.await()
//
//            assertThat(count, `is`(2))
//            assertThat(result, `is`("MyString"))
//        }
//    }
}