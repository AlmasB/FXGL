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

package com.almasb.fxgl.ecs

import com.almasb.fxgl.ecs.diff.InjectableControl
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InjectionTest {

    @Test
    fun `Component fields are injected`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))
        entity.addControl(EntityTest.CustomDataControl("InjectControl"))
        entity.addControl(InjectableControl())

        Assert.assertThat(entity.hasControl(InjectableControl::class.java), Is.`is`(true))
    }

    @Test(expected = RuntimeException::class)
    fun `Throw if component not present`() {
        val entity = Entity()
        entity.addControl(EntityTest.CustomDataControl("InjectControl"))
        entity.addControl(InjectableControl())
    }

    @Test(expected = RuntimeException::class)
    fun `Throw if control not present`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))
        entity.addControl(InjectableControl())
    }
}