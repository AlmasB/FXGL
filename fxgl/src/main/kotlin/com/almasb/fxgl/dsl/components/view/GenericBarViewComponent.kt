/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components.view

import com.almasb.fxgl.ui.Position
import com.almasb.fxgl.ui.ProgressBar
import javafx.beans.property.DoubleProperty
import javafx.scene.paint.Color

/**
 * A general bar view component to display properties like health, mana, or energy.
 * Can be fully customized using its [bar] property.
 * @author Marvin Buff (marvinbuff@hotmail.com)
 */
open class GenericBarViewComponent constructor(
        x: Double,
        y: Double,
        color: Color,
        initialValue: Double,
        maxValue: Double = initialValue
) : ChildViewComponent(x, y, false) {

    /**
     * Utility constructor to setup the [GenericBarViewComponent] bound to the given [property].
     */
    constructor(x: Double, y: Double, color: Color, property: DoubleProperty
    ) : this(x, y, color, property.value) {
        valueProperty().bind(property)
    }

    val bar = ProgressBar()

    init {

        bar.setMinValue(0.0)
        bar.setMaxValue(maxValue)
        bar.setWidth(100.0)
        bar.isLabelVisible = false
        bar.setLabelPosition(Position.RIGHT)
        bar.setFill(color)
        bar.currentValue = initialValue

        viewRoot.children.add(bar)

    }

    fun valueProperty(): DoubleProperty = bar.currentValueProperty()
    fun maxValueProperty(): DoubleProperty = bar.maxValueProperty()

}