/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.core.collection.PropertyMap
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialogueScriptRunnerTest {

    private lateinit var runner: DialogueScriptRunner

    @BeforeEach
    fun setUp() {
        val map = PropertyMap()

        runner = DialogueScriptRunner(map) { funcName, args -> }
    }

    @Test
    fun `Boolean function returns false if cannot be parsed`() {
        assertFalse(runner.callBooleanFunction("bla-bla"))
    }

    @Test
    fun `Call a boolean function with equality check operators`() {
        assertTrue(runner.callBooleanFunction("5 == 5"))
        assertFalse(runner.callBooleanFunction("5 == 7"))

        assertTrue(runner.callBooleanFunction("5 >= 5"))
        assertFalse(runner.callBooleanFunction("5 >= 7"))

        assertTrue(runner.callBooleanFunction("5 <= 5"))
        assertFalse(runner.callBooleanFunction("5 <= 3"))

        assertTrue(runner.callBooleanFunction("5 > 3"))
        assertFalse(runner.callBooleanFunction("5 > 7"))

        assertTrue(runner.callBooleanFunction("5 < 7"))
        assertFalse(runner.callBooleanFunction("5 < 3"))
    }
}