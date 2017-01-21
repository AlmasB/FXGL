/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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