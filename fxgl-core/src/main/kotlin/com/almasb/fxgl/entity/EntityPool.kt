/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class EntityPool {

    private val pool = hashMapOf<String, Deque<Entity>>()

    fun put(spawnName: String, entity: Entity) {
        var queue: Deque<Entity>? = pool[spawnName]

        if (queue == null) {
            queue = ArrayDeque()

            pool[spawnName] = queue
        }

        queue.addLast(entity)
    }

    fun take(spawnName: String): Entity? {
        val queue: Deque<Entity>? = pool[spawnName]

        if (queue != null && queue.isNotEmpty()) {
            return queue.removeFirst()
        }

        return null
    }
}