/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.core.View
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityView
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.geometry.Point2D
import javafx.scene.Node

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityBuilder {
    private val entity = Entity()

    fun from(data: SpawnData) = this.also {
        at(data.x, data.y)

        data.data.forEach { entity.setProperty(it.key, it.value) }
    }

    fun type(t: Enum<*>) = this.also {
        entity.type = t
    }

    fun at(x: Double, y: Double) = this.also {
        entity.setPosition(x, y)
    }

    fun at(p: Vec2) = this.also {
        entity.setPosition(p)
    }

    fun at(p: Point2D) = this.also {
        entity.setPosition(p)
    }

    fun rotate(angle: Double) = this.also {
        entity.rotateBy(angle)
    }

    fun scale(x: Double, y: Double) = this.also {
        entity.scaleX = x
        entity.scaleY = y
    }

    fun scale(scale: Point2D) = this.also {
        scale(scale.x, scale.y)
    }

    fun opacity(value: Double) = this.also {
        entity.viewComponent.opacity.value = value
    }

    fun bbox(box: HitBox) = this.also {
        entity.boundingBoxComponent.addHitBox(box)

        val center = entity.boundingBoxComponent.centerLocal

        entity.transformComponent.scaleOrigin = center
        entity.transformComponent.rotationOrigin = center
    }

    fun view(node: Node) = this.also {
        if (node is View) {
            entity.viewComponent.view = node
        } else {
            entity.viewComponent.setViewFromNode(node)
        }

//        entityView.clearChildren()
//
//        if (node is View) {
//            entity.view = node
//        } else {
//            entityView.addNode(node)
//            entity.view = entityView
//        }
    }

    fun viewWithBBox(node: Node) = this.also {
        view(node)

        entity.boundingBoxComponent.clearHitBoxes()

        val w = node.layoutBounds.width
        val h = node.layoutBounds.height

        bbox(HitBox("__VIEW__", BoundingShape.box(w, h)))
    }

    fun view(textureName: String) = this.also {
        view(FXGL.texture(textureName))
    }

    fun viewWithBBox(textureName: String) = this.also {
        viewWithBBox(FXGL.texture(textureName))
    }

    fun zIndex(z: Int) = this.also {
        entity.transformComponent.z = z
    }

    fun with(vararg comps: Component) = this.also {
        comps.forEach { entity.addComponent(it) }
    }

    fun with(propertyKey: String, propertyValue: Any) = this.also {
        entity.setProperty(propertyKey, propertyValue)
    }

    fun build() = entity

    fun buildAndAttach() = entity.also { FXGL.getGameWorld().addEntity(it) }
}