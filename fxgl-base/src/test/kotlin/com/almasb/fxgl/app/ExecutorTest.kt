/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTimeout
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.time.Duration.ofSeconds
import java.util.concurrent.CountDownLatch


/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ExecutorTest {

    private lateinit var executor: Executor

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

    @AfterEach
    fun `tearDown`() {
        executor.shutdownNow()
    }
}