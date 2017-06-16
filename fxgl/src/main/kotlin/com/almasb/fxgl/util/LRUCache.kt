/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util

import java.util.*

/**
 * Simple LRU cache based on LinkedHashMap.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LRUCache<K, V>(
        /**
         * Max num of elements to keep in cache.
         */
        val maxSize: Int) {

    private val cache = object : LinkedHashMap<K, V>(maxSize + 1, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>) = size > maxSize
    }

    fun put(key: K, value: V) {
        cache.put(key, value)
    }

    fun get(key: K) = cache[key]

    /**
     * Clear the cache.
     */
    fun clear() = cache.clear()
}