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

package com.almasb.fxgl.ai.goap

import java.util.*

/**
 * A lightweight version of GameState.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class State(initialData: Map<String, Any> = emptyMap()) {

    private val data = HashMap<String, Any>()

    constructor(copy: State) : this(copy.data)

    init {
        data.putAll(initialData)
    }

    fun add(key: String, value: Any) {
        data.put(key, value)
    }

    fun remove(key: String) {
        data.remove(key)
    }

    /**
     * Check that all items in [this] are in [other].
     * If just one does not match or is not there
     * then this returns false.
     */
    fun isIn(other: State): Boolean {
        for ((k, v) in data) {
            val otherV = other.data[k]
            if (v != otherV) {
                return false
            }
        }

        return true
    }

    /**
     * Apply the state data from [other] to [this].
     */
    fun update(other: State) {
        for ((k, v) in other.data) {
            data[k] = v
        }
    }
}