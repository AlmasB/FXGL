/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components.view

import com.almasb.fxgl.ui.Position
import com.almasb.fxgl.ui.ProgressBar
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

/**
 * A general bar view component to display properties like health, mana, or energy.
 * Can be fully customized using its [bar] and [maxValue] property.
 * @author Marvin Buff (marvinbuff@hotmail.com)
 */
open class GenericBarViewComponent(x: Double, y: Double, valueProperty: SimpleIntegerProperty, color: Color) : ChildViewComponent(x, y, false) {

    val bar = ProgressBar()
    val maxValue = SimpleIntegerProperty(valueProperty.get())

    init {

        bar.setMinValue(0.0)
        bar.setMaxValue(100.0)
        bar.setWidth(100.0)
        bar.isLabelVisible = false
        bar.setLabelPosition(Position.RIGHT)
        bar.setFill(color)

        bar.currentValueProperty().bind(
                valueProperty.divide(maxValue).multiply(100)
        )

        viewRoot.children.add(bar)
    }
}