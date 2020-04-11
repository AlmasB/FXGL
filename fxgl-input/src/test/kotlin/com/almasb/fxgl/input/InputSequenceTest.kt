/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyCode
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputSequenceTest {

    @Test
    fun `Input sequence correctly matches given queue`() {
        val seq = InputSequence(KeyCode.F, KeyCode.X, KeyCode.G, KeyCode.L)

        assertThat(seq.lastKey, `is`(KeyCode.L))

        val correctQueue1 = ArrayDeque<KeyCode>()
        correctQueue1.addAll(arrayOf(KeyCode.F, KeyCode.X, KeyCode.G, KeyCode.L))

        assertTrue(seq.matches(correctQueue1))

        val correctQueue2 = ArrayDeque<KeyCode>()
        correctQueue2.addAll(arrayOf(KeyCode.A, KeyCode.K, KeyCode.F, KeyCode.X, KeyCode.G, KeyCode.L))

        assertTrue(seq.matches(correctQueue2))

        val incorrectQueue1 = ArrayDeque<KeyCode>()
        incorrectQueue1.addAll(arrayOf(KeyCode.F, KeyCode.G, KeyCode.X, KeyCode.L))

        assertFalse(seq.matches(incorrectQueue1))

        val incorrectQueue2 = ArrayDeque<KeyCode>()
        incorrectQueue2.addAll(arrayOf(KeyCode.K))

        assertFalse(seq.matches(incorrectQueue2))
    }
}