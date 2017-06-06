/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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