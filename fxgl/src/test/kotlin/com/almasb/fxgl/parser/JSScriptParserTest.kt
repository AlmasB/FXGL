/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser

import com.almasb.fxgl.app.FXGL
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class JSScriptParserTest {

    private val JS_DATA = "function test() {\n" + "    return \"JSTestInline\"\n" + "}\n"

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGL.configure(com.almasb.fxgl.app.MockApplicationModule.get())
        }
    }

    @Test
    fun `Invoke function from external source`() {
        val jsParser = JavaScriptParser("test.js")
        val returnValue = jsParser.callFunction<String>("test")

        assertThat(returnValue, `is`("JSTest"))
    }

    @Test
    fun `Invoke function from internal source`() {
        val jsParser = JavaScriptParser(JS_DATA)
        val returnValue = jsParser.callFunction<String>("test")

        assertThat(returnValue, `is`("JSTestInline"))
    }

    @Test
    fun `Fail if cannot be parsed`() {
        assertThrows(IllegalArgumentException::class.java, {
            JavaScriptParser("bla-bla-blah() function")
        })
    }
}