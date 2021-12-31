/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.core.collection.PropertyMap
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
        map.setValue("someInt", 5)
        map.setValue("someDouble", 3.0)
        map.setValue("someString", "hello")
        map.setValue("isAlive", true)
        map.setValue("isSleeping", false)

        runner = DialogueScriptRunner(map, PropertyMap(), object : FunctionCallHandler() {
            override fun handle(functionName: String, args: Array<String>): Any {
                var result: Any = ""

                if (functionName == "hasItem") {
                    result = args[0] == "400"
                }

                return result
            }
        })
    }

    @Test
    fun `Replace variables in text`() {
        var result = runner.replaceVariablesInText("Test \$someInt \$isSleeping \$isAlive \$someString \$someDouble")

        assertThat(result, `is`("Test 5 false true hello 3.0"))

        result = runner.replaceVariablesInText("Test \$someInt.")

        assertThat(result, `is`("Test 5."))
    }

    @Test
    fun `Call a boolean function`() {
        assertTrue(runner.callBooleanFunction("hasItem 400"))
        assertFalse(runner.callBooleanFunction("hasItem 300"))
    }

    @Test
    fun `Boolean function returns false if cannot be parsed`() {
        assertFalse(runner.callBooleanFunction("bla-bla"))
    }

    @Test
    fun `Call a boolean function with single boolean variables`() {
        assertTrue(runner.callBooleanFunction("isAlive"))
        assertFalse(runner.callBooleanFunction("isSleeping"))
    }

    @Test
    fun `Call a boolean function with equality check operators`() {
        assertTrue(runner.callBooleanFunction("5.0 == 5.0"))
        assertFalse(runner.callBooleanFunction("5.0 == 7"))

        assertTrue(runner.callBooleanFunction("5 >= 5.0"))
        assertFalse(runner.callBooleanFunction("5 >= 7"))

        assertTrue(runner.callBooleanFunction("5 <= 5"))
        assertFalse(runner.callBooleanFunction("5 <= 3"))

        assertTrue(runner.callBooleanFunction("5 > 3"))
        assertFalse(runner.callBooleanFunction("5 > 7"))

        assertTrue(runner.callBooleanFunction("5 < 7"))
        assertFalse(runner.callBooleanFunction("5 < 3"))

        assertTrue(runner.callBooleanFunction("\$someInt == 5"))
        assertTrue(runner.callBooleanFunction("\$someDouble == 3.0"))
        assertTrue(runner.callBooleanFunction("\$someDouble < 3.5"))
    }

    @Test
    fun `Call a function with assignment statement`() {
        runner.callFunction("isPoisoned = false")
        assertFalse(runner.callBooleanFunction("isPoisoned"))

        runner.callFunction("isPoisoned = true")
        assertTrue(runner.callBooleanFunction("isPoisoned"))

        runner.callFunction("name = Test Name")
        assertTrue(runner.callBooleanFunction("\$name == Test Name"))

        runner.callFunction("someInt = 66")
        assertTrue(runner.callBooleanFunction("\$someInt == 66"))
    }
}