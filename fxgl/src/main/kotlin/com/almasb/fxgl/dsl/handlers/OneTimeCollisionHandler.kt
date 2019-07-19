/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.handlers

import com.almasb.fxgl.core.util.BiConsumer
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.components.CollidableComponent
import com.almasb.fxgl.physics.CollisionHandler

/**
 * A collision handler between a generic entity and a one-time collidable entity.
 * Once a collision happens, the one-time collidable entity will not be collidable any more.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OneTimeCollisionHandler(entityType: Any,
                              oneTimeCollidableType: Any,
                              private val action: BiConsumer<Entity, Entity>

) : CollisionHandler(entityType, oneTimeCollidableType) {

    override fun onCollisionBegin(e: Entity, oneTimeCollidable: Entity) {
        oneTimeCollidable.getComponentOptional(CollidableComponent::class.java)
                .ifPresent { c -> c.value = false }

        action.accept(e, oneTimeCollidable)
    }
}
