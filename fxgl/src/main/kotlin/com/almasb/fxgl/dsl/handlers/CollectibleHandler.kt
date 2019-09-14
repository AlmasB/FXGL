/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.handlers

import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.dsl.play
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.physics.CollisionHandler

/**
 * Generic collectible collision handler.
 * The collectible will be removed from the world when
 * the collector collides with it.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CollectibleHandler
@JvmOverloads constructor(collectorType: Any, collectibleType: Any,
                          private val soundName: String = "",
                          private val action: Consumer<Entity> = Consumer {}) : CollisionHandler(collectorType, collectibleType) {

    override fun onCollisionBegin(collector: Entity, collectible: Entity) {
        if (soundName.isNotEmpty()) {
            play(soundName)
        }

        action.accept(collectible)

        collectible.removeFromWorld()
    }
}