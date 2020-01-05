/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.CollidableComponent
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.BoundingShape.Companion.box
import com.almasb.fxgl.physics.HitBox
import com.almasb.fxgl.physics.PhysicsComponent
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

        if (data.hasKey("type")) {
            val value = data.get<Any>("type")
            if (value is Enum<*>) {
                type(value)
            }
        }

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

    fun atAnchored(localAnchor: Point2D, position: Point2D) = this.also {
        entity.localAnchor = localAnchor
        entity.anchoredPosition = position
    }

    fun rotationOrigin(x: Double, y: Double) = this.also {
        entity.transformComponent.rotationOrigin = Point2D(x, y)
    }

    fun rotationOrigin(point: Point2D) = this.also {
        entity.transformComponent.rotationOrigin = point
    }

    fun rotate(angle: Double) = this.also {
        entity.rotateBy(angle)
    }

    fun scaleOrigin(x: Double, y: Double) = this.also {
        entity.transformComponent.scaleOrigin = Point2D(x, y)
    }

    fun scaleOrigin(point: Point2D) = this.also {
        entity.transformComponent.scaleOrigin = point
    }

    fun scale(x: Double, y: Double) = this.also {
        entity.scaleX = x
        entity.scaleY = y
    }

    fun scale(scale: Point2D) = this.also {
        scale(scale.x, scale.y)
    }

    fun opacity(value: Double) = this.also {
        entity.viewComponent.opacity = value
    }

    fun bbox(box: HitBox) = this.also {
        entity.boundingBoxComponent.addHitBox(box)

        val center = entity.boundingBoxComponent.centerLocal

        entity.transformComponent.scaleOrigin = center
        entity.transformComponent.rotationOrigin = center
    }

    fun view(node: Node) = this.also {
        entity.viewComponent.addChild(node)
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

    fun collidable() = with(CollidableComponent(true))

    fun with(vararg comps: Component) = this.also {
        comps.forEach { entity.addComponent(it) }
    }

    fun with(propertyKey: String, propertyValue: Any) = this.also {
        entity.setProperty(propertyKey, propertyValue)
    }

    fun build() = entity

    fun buildAndAttach() = entity.also { FXGL.getGameWorld().addEntity(it) }

    /**
     * Create an entity with a bounding box around the screen with given thickness.
     *
     * @param thickness thickness of hit boxes around the screen
     * @return entity with screen bounds
     */
    fun buildScreenBounds(thickness: Double): Entity {
        val w = FXGL.getAppWidth().toDouble()
        val h = FXGL.getAppHeight().toDouble()
        
        return bbox(HitBox("LEFT",  Point2D(-thickness, 0.0), box(thickness, h)))
                .bbox(HitBox("RIGHT", Point2D(w, 0.0), box(thickness, h)))
                .bbox(HitBox("TOP",   Point2D(0.0, -thickness), box(w, thickness)))
                .bbox(HitBox("BOT",   Point2D(0.0, h), box(w, thickness)))
                .with(PhysicsComponent())
                .build()
    }

    fun buildScreenBoundsAndAttach(thickness: Double) = entity.also { FXGL.getGameWorld().addEntity(buildScreenBounds(thickness)) }
}