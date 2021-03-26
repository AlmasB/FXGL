/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.Copyable
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CopyableComponent
import com.almasb.fxgl.entity.component.Required
import javafx.scene.Node

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal object EntityHelper {

    fun copy(entity: Entity): Entity {
        val copy = Entity()

        // TODO: other transform properties
        copy.type = entity.type
        copy.position = entity.position
        copy.rotation = entity.rotation

        entity.viewComponent.children.forEach {
            if (it is Copyable<*>) {
                val copyView = it.copy()

                copy.viewComponent.addChild(copyView as Node)
            }
        }

        entity.components
                .filterIsInstance<CopyableComponent<*>>()
                .map { it.copy() }
                .forEach { copy.addComponent(it) }

        // TODO: implement proper copy(), what to do if a Component is not copyable?
//
//        entity.boundingBoxComponent.hitBoxesProperty().forEach {
//            copy.boundingBoxComponent.addHitBox(it.copy())
//        }
//
//        // find components without requirements, add them first
//        // then the other ones
//        // this is flawed, we actually need to sort this, so that we have a correct dependency order
//        // https://github.com/AlmasB/FXGL/issues/529
//        val map = entity.components
//                .filterIsInstance<CopyableComponent<*>>()
//                .groupBy { it.javaClass.getAnnotation(Required::class.java) != null }
//
//        val components1 = map[true]
//        val components2 = map[false]
//
//        components2?.forEach {
//            if (!copy.hasComponent(it.javaClass as Class<out Component>)) {
//                copy.addComponent(it.copy())
//            }
//        }
//
//        components1?.forEach {
//            if (!copy.hasComponent(it.javaClass as Class<out Component>)) {
//                copy.addComponent(it.copy())
//            }
//        }

        return copy
    }
}