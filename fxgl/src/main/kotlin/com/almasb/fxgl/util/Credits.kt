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

package com.almasb.fxgl.util


import java.util.*

/**
 * Simple data structure to contain a list of credits.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Credits {

    /**
     * Unmodifiable list of credits.
     */
    val list: List<String>

    /**
     * Constructs credits from given list of names.
     */
    constructor(list: List<String>) {
        val newList = ArrayList(list)
        with(newList) {
            add("")
            add("Powered by FXGL ${com.almasb.fxgl.util.Version.getAsString()}")
            add("Graphics: JavaFX ${com.almasb.fxgl.util.Version.getJavaFXAsString()}")
            add("Physics: JBox2D (jbox2d.org) ${com.almasb.fxgl.util.Version.getJBox2DAsString()}")
            add("Written in: Java ${com.almasb.fxgl.util.Version.getJavaFXAsString()}, Kotlin ${com.almasb.fxgl.util.Version.getKotlinAsString()}")
            add("Author: Almas Baimagambetov")
            add("https://github.com/AlmasB/FXGL")
            add("")
        }

        this.list = Collections.unmodifiableList(newList)
    }

    /**
     * Copy constructor.
     */
    constructor(copy: Credits) {
        list = copy.list
    }
}