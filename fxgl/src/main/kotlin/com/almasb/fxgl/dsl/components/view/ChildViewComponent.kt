/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components.view

import com.almasb.fxgl.entity.component.Component
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Group

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class ChildViewComponent
@JvmOverloads constructor(x: Double = 0.0,
                          y: Double = 0.0,
                          val isTransformApplied: Boolean = true) : Component() {

    private val propX: DoubleProperty = SimpleDoubleProperty(x)
    private val propY: DoubleProperty = SimpleDoubleProperty(y)

    var x: Double
        get() = propX.value
        set(value) { propX.value = value }

    var y: Double
        get() = propY.value
        set(value) { propY.value = value }

    val viewRoot = Group()

    init {
        viewRoot.translateXProperty().bind(propX)
        viewRoot.translateYProperty().bind(propY)
    }

    override fun onAdded() {
        entity.viewComponent.addChild(viewRoot, isTransformApplied)
    }

    override fun onRemoved() {
        viewRoot.translateXProperty().unbind()
        viewRoot.translateYProperty().unbind()
        entity.viewComponent.removeChild(viewRoot)
    }
}