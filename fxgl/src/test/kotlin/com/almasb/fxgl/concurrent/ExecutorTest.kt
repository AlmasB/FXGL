/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.concurrent

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.time.Duration.ofSeconds


/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ExecutorTest {

    private val executor = FXGL.getExecutor()

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
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
}