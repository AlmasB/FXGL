/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.view

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.scene.paint.*
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle

/**
 * View for a mouse button.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MouseButtonView
@JvmOverloads constructor(button: MouseButton,
                          color: Color = Color.ORANGE,
                          size: Double = 24.0) : Pane() {

    private val border = Rectangle(size, size * 1.5)

    fun colorProperty(): ObjectProperty<Paint> = border.strokeProperty()

    var color: Paint
        get() = border.stroke
        set(value) { border.stroke = value }

    private val bgColorProp = SimpleObjectProperty(Color.BLACK)

    //fun backgroundColorProperty(): ObjectProperty<Color> = bgColorProp

    var backgroundColor: Color
        get() = bgColorProp.value
        set(value) { bgColorProp.value = value }

    init {
        border.fillProperty().bind(bgColorProp)
        border.stroke = color
        border.strokeWidth = size / 7
        border.arcWidth = size / 1.5
        border.arcHeight = size / 1.5

        val borderTop = Rectangle(size, size * 1.5)
        borderTop.fill = null
        borderTop.strokeProperty().bind(border.strokeProperty())
        borderTop.strokeWidth = size / 7
        borderTop.arcWidth = size / 1.5
        borderTop.arcHeight = size / 1.5

        val line1 = Line(size / 2, 0.0, size / 2, size / 5)
        line1.strokeProperty().bind(border.strokeProperty())
        line1.strokeWidth = size / 7

        val ellipse = Rectangle(size / 6, size / 6 * 1.5)
        ellipse.fill = null
        ellipse.strokeProperty().bind(border.strokeProperty())
        ellipse.strokeWidth = size / 10
        ellipse.arcWidth = size / 1.5
        ellipse.arcHeight = size / 1.5
        ellipse.translateX = size / 2 - size / 6 / 2
        ellipse.translateY = size / 5

        val line2 = Line(size / 2, size / 5 * 2.75, size / 2, size / 5 * 5)
        line2.stroke = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, color), Stop(0.75, backgroundColor))
        line2.strokeWidth = size / 7

        border.strokeProperty().addListener { _, _, paint ->
            if (paint is Color)
                line2.stroke = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, paint), Stop(0.75, backgroundColor))
        }

        children.addAll(border, line1, line2, ellipse, borderTop)

        when(button) {
            MouseButton.PRIMARY -> {
                val highlight = Rectangle(size / 2.5, size / 6 * 3.5)
                highlight.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, backgroundColor), Stop(0.25, color), Stop(0.8, color), Stop(0.9, backgroundColor))
                highlight.arcWidth = size / 4
                highlight.arcHeight = size / 4
                highlight.translateX = size / 20
                highlight.translateY = size / 8

                border.strokeProperty().addListener { _, _, paint ->
                    if (paint is Color)
                        highlight.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, backgroundColor), Stop(0.25, paint), Stop(0.8, paint), Stop(0.9, backgroundColor))
                }

                children.add(1, highlight)
            }

            MouseButton.SECONDARY -> {
                val highlight = Rectangle(size / 2.5, size / 6 * 3.5)
                highlight.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, backgroundColor), Stop(0.25, color), Stop(0.8, color), Stop(0.9, backgroundColor))
                highlight.arcWidth = size / 4
                highlight.arcHeight = size / 4
                highlight.translateX = size - size / 20 - highlight.width
                highlight.translateY = size / 8

                border.strokeProperty().addListener { _, _, paint ->
                    if (paint is Color)
                        highlight.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, backgroundColor), Stop(0.25, paint), Stop(0.8, paint), Stop(0.9, backgroundColor))
                }

                children.add(1, highlight)
            }

            else -> {
                throw IllegalArgumentException("View for $button type is not (yet?) supported")
            }
        }
    }
}