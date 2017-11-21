/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

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

        entity.components
                .filterIsInstance<CopyableComponent<*>>()
                .forEach {
                    if (!copy.hasComponent(it.javaClass as Class<out Component>)) {
                        copy.addComponent(it.copy())
                    }
                }

        entity.controls
                .filterIsInstance<CopyableControl<*>>()
                .forEach { copy.addControl(it.copy()) }

        return copy
    }
}