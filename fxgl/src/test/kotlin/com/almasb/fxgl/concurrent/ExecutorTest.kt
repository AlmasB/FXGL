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
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.rules.Timeout
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ExecutorTest {

    private val executor = FXGL.getExecutor()

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
    }

    @Rule
    @JvmField var globalTimeout: TestRule = Timeout(2, TimeUnit.SECONDS)

    @Test
    fun `Test that execute() runs in a different thread`() {
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

    @Test
    fun `Test schedule() runs after given delay`() {
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