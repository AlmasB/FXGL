/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal object EntityCopier {

    fun copy(entity: Entity): Entity {
        val copy = Entity()

        entity.components
                .filterIsInstance<CopyableComponent<*>>()
                .forEach { copy.addComponent(it.copy()) }

        entity.controls
                .filterIsInstance<CopyableControl<*>>()
                .forEach { copy.addControl(it.copy()) }

        return copy
    }
}