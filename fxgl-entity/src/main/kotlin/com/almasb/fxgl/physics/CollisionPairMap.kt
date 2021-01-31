/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.entity.Entity
import java.util.*

/**
 * Data structure for storing active collisions.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class CollisionPairMap(capacity: Int) {

    private val collisionsMap = HashMap<Int, CollisionPair>(capacity)

    val values
        get() = collisionsMap.values

    /**
     * Clear all registered collisions.
     */
    fun clear() {
        collisionsMap.clear()
    }

    /**
     * @return a collision pair for [e1] [e2] or null if no collision was registered
     */
    fun get(e1: Entity, e2: Entity): CollisionPair? {
        return collisionsMap[hash(e1, e2)]
    }

    /**
     * Add a new collision.
     */
    fun put(pair: CollisionPair) {
        collisionsMap[hash(pair.a, pair.b)] = pair
    }

    /**
     * Remove an existing collision.
     */
    fun remove(pair: CollisionPair) {
        collisionsMap.remove(hash(pair.a, pair.b))
    }

    /**
     * Preconditions:
     * e1 !== e2
     * e1 and e2 produce unique hashcodes
     */
    private fun hash(e1: Entity, e2: Entity): Int {
        val hash1 = e1.hashCode()
        val hash2 = e2.hashCode()

        return if (hash1 > hash2) {
            31 * (31 + hash1) + hash2
        } else {
            31 * (31 + hash2) + hash1
        }
    }
}