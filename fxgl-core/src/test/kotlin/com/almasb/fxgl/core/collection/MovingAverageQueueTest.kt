/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.core.collection

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MovingAverageQueueTest {

    private lateinit var queue: MovingAverageQueue

    @BeforeEach
    fun setUp() {
        queue = MovingAverageQueue(100)
    }

    @Test
    fun `Average`() {
        repeat(50) {
            queue.put(25.0)
        }

        assertThat(queue.average, Matchers.closeTo(25.0, 0.0001))

        repeat(10) {
            queue.put(27.0)
        }

        assertThat(queue.average, Matchers.closeTo(25.33, 0.01))

        repeat(40) {
            queue.put(27.0)
        }

        assertThat(queue.average, Matchers.closeTo(26.0, 0.0001))

        repeat(100) {
            queue.put(30.0)
        }

        assertThat(queue.average, Matchers.closeTo(30.0, 0.0001))
    }
}