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

package com.almasb.fxgl.entity.component

import org.junit.Test
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IDComponentTest {

    @Test
    fun `Equality`() {
        val id1 = com.almasb.fxgl.entity.component.IDComponent("Test", 0)
        val id2 = com.almasb.fxgl.entity.component.IDComponent("Test", 1)

        assertThat(id1.name, `is`(id2.name))
        assertThat(id1.id, `is`(not(id2.id)))
        assertFalse(id1 == id2)
        assertThat(id1.fullID, `is`(not(id2.fullID)))
        assertThat(id1.hashCode(), `is`(not(id2.hashCode())))
    }

    @Test
    fun `Serialization`() {
        // TODO:
//        val id1 = IDComponent("Test", 0)
//
//        val bundle = Bundle()
    }
}