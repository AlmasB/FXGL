/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.serialization

import java.io.Serializable

/**
 * Bundle is used to store values mapped with certain keys.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Bundle(val name: String) : Serializable {

    companion object {
        private val serialVersionUID = 1L
    }

    val data = hashMapOf<String, Serializable>()

    /**
     * Store a [value] with given [key].
     */
    fun put(key: String, value: Serializable) {
        data[key] = value
    }

    /**
     * Retrieve a value with given [key].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T {
        return data[key] as T
    }

    fun exists(key: String): Boolean {
        return data.containsKey(key)
    }

    override fun toString() = "Bundle $name: $data"
}