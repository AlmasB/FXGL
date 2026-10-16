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
    
    private val map = HashMap<UnorderedPair<K>, V>(capacity)

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
        return map[UnorderedPair(key1, key2)]
    }

    /**
     * Add a new mapping from [key1] [key2] to [value].
     */
    fun put(key1: K, key2: K, value: V) {
        map[UnorderedPair(key1, key2)] = value
    }

    /**
     * Remove an existing mapping whose key is [key1] [key2].
     */
    fun remove(key1: K, key2: K) {
        map.remove(UnorderedPair(key1, key2))
    }

    /**
     * Unordered pair key. Distinct pairs with the same hash must remain distinct.
     */
    private class UnorderedPair<K>(val a: K, val b: K) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is UnorderedPair<*>) return false

            return (a == other.a && b == other.b) || (a == other.b && b == other.a)
        }

        override fun hashCode(): Int {
            val hash1 = a.hashCode()
            val hash2 = b.hashCode()

            return if (hash1 > hash2) {
                31 * (31 + hash1) + hash2
            } else {
                31 * (31 + hash2) + hash1
            }
        }
    }
}