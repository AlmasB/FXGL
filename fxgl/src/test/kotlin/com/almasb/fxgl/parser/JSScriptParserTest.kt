/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser

import com.almasb.fxgl.app.FXGL
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.BeforeClass
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class JSScriptParserTest {

    private val JS_DATA = "function test() {\n" + "    return \"JSTestInline\"\n" + "}\n"

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(com.almasb.fxgl.app.MockApplicationModule.get())
        }
    }

    @Test
    fun `Invoke function from external source`() {
        val jsParser = com.almasb.fxgl.parser.JavaScriptParser("test.js")
        val returnValue = jsParser.callFunction<String>("test")

        assertThat(returnValue, `is`("JSTest"))
    }

    @Test
    fun `Invoke function from internal source`() {
        val jsParser = com.almasb.fxgl.parser.JavaScriptParser(JS_DATA)
        val returnValue = jsParser.callFunction<String>("test")

        assertThat(returnValue, `is`("JSTestInline"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Fail if cannot be parsed`() {
        com.almasb.fxgl.parser.JavaScriptParser("bla-bla-blah() function")
    }
}