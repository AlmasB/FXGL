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

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.parser.tiled.TiledObject
import javafx.geometry.Point2D

/**
 * Specifies data used to spawn a particular type of entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class SpawnData(val x: Double, val y: Double) {

    private val data = ObjectMap<String, Any>()

    constructor(position: Point2D) : this(position.x, position.y)

    constructor(tiledObject: TiledObject) : this(tiledObject.x.toDouble(), tiledObject.y.toDouble()) {
        put("type", tiledObject.type)
        put("width", tiledObject.width)
        put("height", tiledObject.height)
        put("rotation", tiledObject.rotation)
        put("id", tiledObject.id)

        tiledObject.properties.forEach { put(it.key, it.value) }
    }

    fun put(key: String, value: Any): SpawnData {
        data.put(key, value)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T {
        val value = data.get(key) ?: throw IllegalArgumentException("Key $key has no associated value!")

        return value as T
    }
}