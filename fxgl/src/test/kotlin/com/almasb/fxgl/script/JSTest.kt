/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.script

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class JSTest {

    private val JS_DATA = "function test() {\n" + "    return \"JSTestInline\"\n" + "}\n"

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @Test
    fun `Invoke function from external source`() {
        val jsParser = FXGL.getAssetLoader().loadScript("test.js")
        val returnValue = jsParser.call<String>("test")

        assertThat(returnValue, `is`("JSTest"))
    }

    @Test
    fun `Invoke function from internal source`() {
        val jsParser = ScriptFactory.fromCode(JS_DATA)
        val returnValue = jsParser.call<String>("test")

        assertThat(returnValue, `is`("JSTestInline"))
    }

    @Test
    fun `Fail if cannot be parsed`() {
        assertThrows(IllegalArgumentException::class.java, {
            ScriptFactory.fromCode("bla-bla-blah() function")
        })
    }

    @Test
    fun `Each script has its scope`() {
        val code = "var i = 345; function set(value) { i = value; } function get() { return i; }"

        val script1 = ScriptFactory.fromCode(code)
        val script2 = ScriptFactory.fromCode(code)

        assertThat(script1.call<Int>("get"), `is`(345))
        assertThat(script2.call<Int>("get"), `is`(345))

        script1.call<Void>("set", 999)

        assertThat(script1.call<Int>("get"), `is`(999))
        assertThat(script2.call<Int>("get"), `is`(345))

        script2.call<Void>("set", -222)

        assertThat(script1.call<Int>("get"), `is`(999))
        assertThat(script2.call<Int>("get"), `is`(-222))
    }

    @Test
    fun `Has function`() {
        val script1 = ScriptFactory.fromCode(JS_DATA)

        assertFalse(script1.hasFunction("hello"))
        assertTrue(script1.hasFunction("test"))
    }
}