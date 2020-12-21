/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import javafx.geometry.Point2D
import javafx.geometry.Point3D

/**
 * Specifies data used to spawn a particular type of entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class SpawnData
@JvmOverloads constructor(
        val x: Double = 0.0,
        val y: Double = 0.0,
        val z: Double = 0.0) {

    constructor(position: Point2D) : this(position.x, position.y)
    constructor(position: Point3D) : this(position.x, position.y, position.z)

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