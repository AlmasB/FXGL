/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.sslogger.Logger
import javafx.geometry.Point2D
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityPreloader(private val spawners: ObjectMap<String, EntitySpawner>) {

    private val log = Logger.get(javaClass)

    private val thread = EntitySpawnerThread(spawners)

    fun isPreloadingEnabled(entityName: String) = thread.preloadedEntities.containsKey(entityName)

    fun addForPreloading(entityAliases: List<String>, numItems: Int) {
        // already added
        if (entityAliases.any { isPreloadingEnabled(it) })
            return

        val queue = ArrayBlockingQueue<Entity>(numItems)

        entityAliases.forEach {
            thread.preloadedEntities.put(it, queue)
        }

        if (!thread.isAlive) {
            log.info("Starting Entity preloader thread")

            thread.start()
        }
    }

    fun obtain(entityName: String, data: SpawnData): Entity {
        val e = thread.preloadedEntities.get(entityName).take()

        if (e.position == Point2D.ZERO) {
            e.x = data.x
            e.y = data.y
        }

        return e
    }

    private class EntitySpawnerThread(val spawners: ObjectMap<String, EntitySpawner>) : Thread("Entity Spawner Thread") {

        /**
         * Maps entity spawner to preloaded entities.
         */
        val preloadedEntities = ObjectMap<String, BlockingQueue<Entity>>()

        init {
            isDaemon = true
        }

        override fun run() {
            while (true) {

                // TODO: this will block on only one entity spawner
                preloadedEntities.forEach {
                    val spawner = spawners[it.key]
                    val queue = it.value

                    //println("SIZE: " + queue.size)

                    queue.put(spawner.apply(SpawnData(0.0, 0.0)))
                }

                // TODO: better way to signal we have entities to load?
//                if (preloadedEntities.isEmpty) {
//                    sleep(2000)
//                }
            }
        }
    }
}