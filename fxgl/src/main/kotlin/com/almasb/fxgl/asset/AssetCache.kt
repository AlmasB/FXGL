/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.asset

import com.almasb.fxgl.core.Disposable
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AssetCache(
        /**
         * Max num of elements to keep in cache.
         */
        val maxSize: Int) {

    private val cache = object : LinkedHashMap<String, Any>(maxSize + 1, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Any>): Boolean {

            if (size > maxSize) {
                if (eldest.value is Disposable) {
                    (eldest.value as Disposable).dispose()
                }

                return true
            }

            return false
        }
    }

    fun put(key: String, value: Any) {
        cache.put(key, value)
    }

    fun get(key: String) = cache[key]

    fun size() = cache.size

    /**
     * Clear the cache.
     */
    fun clear() = cache.clear()
}