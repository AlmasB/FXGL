/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CopyableComponent
import com.almasb.fxgl.entity.component.Required

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal object EntityCopier {

    fun copy(entity: Entity): Entity {
        val copy = Entity()

        copy.type = entity.type
        copy.position = entity.position
        copy.rotation = entity.rotation

        entity.boundingBoxComponent.hitBoxesProperty().forEach {
            copy.boundingBoxComponent.addHitBox(it.copy())
        }

        // find components without requirements, add them first
        // then the other ones
        // this is flawed, we actually need to sort this, so that we have a correct dependency order
        // https://github.com/AlmasB/FXGL/issues/529
        val map = entity.components
                .filterIsInstance<CopyableComponent<*>>()
                .groupBy { it.javaClass.getAnnotation(Required::class.java) != null }

        val components1 = map[true]
        val components2 = map[false]

        components2?.forEach {
            if (!copy.hasComponent(it.javaClass as Class<out Component>)) {
                copy.addComponent(it.copy())
            }
        }

        components1?.forEach {
            if (!copy.hasComponent(it.javaClass as Class<out Component>)) {
                copy.addComponent(it.copy())
            }
        }

        return copy
    }
}