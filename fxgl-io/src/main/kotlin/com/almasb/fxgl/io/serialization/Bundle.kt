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

package com.almasb.fxgl.io.serialization

import org.apache.logging.log4j.LogManager
import java.io.Serializable
import java.util.*

/**
 * Bundle is used to store values mapped with certain keys.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Bundle(val name: String) : Serializable {

    companion object {
        private val serialVersionUID = 1L

        private val log = LogManager.getLogger(Bundle::class.java)
    }

    private val data = HashMap<String, Any>()

    /**
     * Store a [value] with given [key].
     */
    fun put(key: String, value: Serializable) {
        data.put(name + "." + key, value)
    }

    /**
     * Retrieve a value with given [key].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T {
        return data[name + "." + key] as T
    }

    /**
     * Logs contents of the bundle.
     */
    fun log() {
        log.debug("Logging bundle: $name")
        data.forEach { k, v -> log.debug("$k=$v") }
    }
}