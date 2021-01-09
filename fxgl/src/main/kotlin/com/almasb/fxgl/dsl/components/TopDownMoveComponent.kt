/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.EntityGroup
import com.almasb.fxgl.entity.component.Component
import kotlin.math.roundToInt

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TopDownMoveComponent(var speed: Double) : Component() {

    private var moveSpeed = 0

    private var collidableEntities: EntityGroup? = null

    override fun onUpdate(tpf: Double) {
        moveSpeed = (speed * tpf).roundToInt()
    }

    fun moveLeft() {
        move({ entity.translateX(-1.0) }, { entity.translateX(1.0) })
    }

    fun moveRight() {
        move({ entity.translateX(1.0) }, { entity.translateX(-1.0) })
    }

    fun moveUp() {
        move({ entity.translateY(-1.0) }, { entity.translateY(1.0) })
    }

    fun moveDown() {
        move({ entity.translateY(1.0) }, { entity.translateY(-1.0) })
    }

    private fun move(moveFunc: () -> Unit, moveBackFunc: () -> Unit) {
        repeat(moveSpeed) {
            moveFunc()

            var isColliding = false

            collidableEntities?.forEach {
                if (it.isColliding(entity)) {
                    isColliding = true
                    return@forEach
                }
            }

            if (isColliding) {
                moveBackFunc()
                return@repeat
            }
        }
    }

    /**
     * Tell the component which entity types to collide against, i.e.
     * during move the entity will be blocked by given entity types.
     */
    fun collidables(vararg entityTypes: Enum<*>): TopDownMoveComponent {
        collidableEntities = entity.world.getGroup(*entityTypes)
        return this
    }

    override fun isComponentInjectionRequired(): Boolean = false
}