/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.ui

import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.effect.DropShadow
import javafx.scene.effect.Glow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.Text
import java.util.function.Consumer

/**
 * Wheel menu with a maximum of 4 selectable elements.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WheelMenu(vararg itemNames: String) : Parent() {

    var selectedItem = SimpleStringProperty()
    var selectionHandler: Consumer<String>? = null

    init {
        if (itemNames.size > 4)
            throw IllegalArgumentException("Max number of items is 4")

        val handler = javafx.event.EventHandler<MouseEvent> { event ->
            selectedItem.set((event.getSource() as QuarterCircle).text.text)
            selectionHandler?.accept(selectedItem.value)
            close()
        }

        val first  = if (itemNames.size > 0) itemNames[0] else ""
        val second = if (itemNames.size > 1) itemNames[1] else ""
        val third  = if (itemNames.size > 2) itemNames[2] else ""
        val fourth = if (itemNames.size > 3) itemNames[3] else ""

        val circle = QuarterCircle(first)
        circle.translateY = 0.0
        circle.setOnMouseClicked(handler)

        val circle2 = QuarterCircle(second)
        circle2.translateX = 50.0
        circle2.translateY = 50.0
        circle2.rotate = 90.0
        circle2.setOnMouseClicked(handler)

        val circle3 = QuarterCircle(third)
        circle3.translateY = 100.0
        circle3.rotate = 180.0
        circle3.text.rotate = 180.0
        circle3.setOnMouseClicked(handler)

        val circle4 = QuarterCircle(fourth)
        circle4.translateX = -50.0
        circle4.translateY = 50.0
        circle4.rotate = -90.0
        circle4.setOnMouseClicked(handler)

        opacity = 0.9

        children.addAll(circle, circle2, circle3, circle4)
    }

    fun isOpen() = isVisible

    fun open() {
        isVisible = true
    }

    fun close() {
        isVisible = false
    }

    private inner class QuarterCircle(name: String) : StackPane() {
        val text: Text

        init {
            val circle = Circle(50.0)
            circle.fill = null
            circle.stroke = Color.BLACK
            circle.strokeWidth = 30.0

            var rect = Rectangle(200.0, 100.0)
            rect.translateX = -100.0

            var innerShape = Shape.subtract(circle, rect)

            rect = Rectangle(100.0, 100.0)
            rect.translateY = -100.0

            innerShape = Shape.subtract(innerShape, rect)
            innerShape.rotate = 45.0
            innerShape.stroke = Color.BLACK

            text = Text(name)
            text.fill = Color.WHITE

            opacity = 0.6

            children.addAll(innerShape, text)

            val gradient = LinearGradient(1.0, 1.0, 0.2, 0.2, true, CycleMethod.NO_CYCLE, *arrayOf(Stop(0.3, Color.GOLD), Stop(0.9, Color.BLACK)))

            val shadow = DropShadow(5.0, Color.WHITE)
            shadow.input = Glow(0.8)

            setOnMouseEntered { event ->
                opacity = 0.9
                innerShape.fill = gradient
                text.font = Font.font("", FontPosture.ITALIC, 12.0)
                text.effect = shadow
            }
            setOnMouseExited { event ->
                opacity = 0.6
                innerShape.fill = Color.BLACK
                text.font = Font.getDefault()
                text.effect = null
            }
        }
    }
}