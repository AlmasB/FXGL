/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import javafx.geometry.Point2D

/**
 * Specifies data used to spawn a particular type of entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class SpawnData(val x: Double, val y: Double) {

    constructor(position: Point2D) : this(position.x, position.y)

    val data = hashMapOf<String, Any>()

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