/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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

    val data = ObjectMap<String, Any>()

    constructor(position: Point2D) : this(position.x, position.y)

    constructor(tiledObject: TiledObject) : this(tiledObject.x.toDouble(),
            // it appears that if object has non-zero gid then its y is flipped
            (tiledObject.y - if (tiledObject.gid == 0) 0 else tiledObject.height).toDouble()) {

        put("name", tiledObject.name)
        put("type", tiledObject.type)
        put("width", tiledObject.width)
        put("height", tiledObject.height)
        put("rotation", tiledObject.rotation)
        put("id", tiledObject.id)
        put("gid", tiledObject.gid)

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

    fun hasKey(key: String): Boolean = data.containsKey(key)
}