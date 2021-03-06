/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

/**
 * A map with where K is an unordered pair (order doesn't matter).
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UnorderedPairMap<K, V>
@JvmOverloads constructor(capacity: Int = 16) {
    
    private val map = HashMap<Int, V>(capacity)

    val values
        get() = map.values
    
    /**
     * Clear all key-value pairs in the map.
     */
    fun clear() {
        map.clear()
    }

    /**
     * @return a value for [key1] [key2] pair or null if no such key exists
     */
    fun get(key1: K, key2: K): V? {
        return map[hash(key1, key2)]
    }

    /**
     * Add a new mapping from [key1] [key2] to [value].
     */
    fun put(key1: K, key2: K, value: V) {
        map[hash(key1, key2)] = value
    }

    /**
     * Remove an existing mapping whose key is [key1] [key2].
     */
    fun remove(key1: K, key2: K) {
        map.remove(hash(key1, key2))
    }

    private fun hash(key1: K, key2: K): Int {
        val hash1 = key1.hashCode()
        val hash2 = key2.hashCode()

        return if (hash1 > hash2) {
            31 * (31 + hash1) + hash2
        } else {
            31 * (31 + hash2) + hash1
        }
    }
}