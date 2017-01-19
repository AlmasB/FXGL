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

        executor.execute {
            id2 = Thread.currentThread().id
        }

        while (id2 == -1L) { }

        assertThat(id2, `is`(not(id1)))
    }

    @Test
    fun `Test schedule() runs after given delay`() {
        val now = System.currentTimeMillis()
        var diff = -1L

        executor.schedule({
            diff = System.currentTimeMillis() - now
        }, Duration.seconds(1.0))

        while (diff == -1L) { }

        // allow +-200ms error
        assertTrue(diff > 800 && diff < 1200)
    }
}