/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.views

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import com.almasb.fxgl.entity.GameWorld
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import java.util.concurrent.Callable

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MinimapView(
        private val world: GameWorld,
        var worldWidth: Double,
        var worldHeight: Double,
        private val width: Double,
        private val height: Double) : Parent(), EntityWorldListener {

    private val entityViews = hashMapOf<Entity, Shape>()

    private val bg = Rectangle(width, height)

    private val bgColorProp = SimpleObjectProperty<Color>(Color.color(0.0, 0.0, 0.0, 0.7))
    private val entityColorProp = SimpleObjectProperty<Color>(Color.RED)

    var backgroundColor: Color
        get() = bgColorProp.value
        set(value) { bgColorProp.value = value }

    var entityColor: Color
        get() = entityColorProp.value
        set(value) { entityColorProp.value = value }

    var entitySize = 4.0

    init {
        bg.fillProperty().bind(bgColorProp)
        bg.stroke = Color.BLACK

        children += bg

        world.entities.forEach { onEntityAdded(it) }

        world.addWorldListener(this)
    }

    override fun onEntityAdded(entity: Entity) {
        val view = createView(entity)
        entityViews[entity] = view
        children += view
    }

    private fun createView(e: Entity): Shape {
        return Rectangle(entitySize, entitySize).also {
            it.translateXProperty().bind(
                    Bindings.createDoubleBinding(Callable {
                        FXGLMath.map(e.x, 0.0, worldWidth, 0.0, width)
                    }, e.xProperty())
            )
            it.translateYProperty().bind(
                    Bindings.createDoubleBinding(Callable {
                        FXGLMath.map(e.y, 0.0, worldHeight, 0.0, height)
                    }, e.yProperty())
            )
            it.fillProperty().bind(entityColorProp)
        }
    }

    override fun onEntityRemoved(entity: Entity) {
        entityViews.remove(entity)?.let { children -= it }
    }
}