/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components.view

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.TransformComponent
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class ChildViewComponent(x: Double, y: Double) : Component() {

    constructor() : this(0.0, 0.0)

    private val propX: DoubleProperty = SimpleDoubleProperty(x)
    private val propY: DoubleProperty = SimpleDoubleProperty(y)

    var x: Double
        get() = propX.value
        set(value) { propX.value = value }

    var y: Double
        get() = propY.value
        set(value) { propY.value = value }

    override fun onAdded() {
        // TODO: add child to view somehow at x,y

        //entity.viewComponent.addChild()
    }
}