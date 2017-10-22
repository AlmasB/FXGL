/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.input.view.MouseButtonView
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.*
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.TextFlow

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLTextFlow : TextFlow() {

    fun append(node: Node): FXGLTextFlow {
        children.add(node)
        return this
    }

    fun append(message: String): FXGLTextFlow {
        return append(message, Color.BLACK, 22.0)
    }

    fun append(message: String, fontSize: Double): FXGLTextFlow {
        return append(message, Color.BLACK, fontSize)
    }

    fun append(key: KeyCode, color: Color): FXGLTextFlow {
        val keyView = KeyView(key, color)
        children.add(keyView)
        return this
    }

    fun append(btn: MouseButton, color: Color): FXGLTextFlow {
        val view = MouseButtonView(btn, color, 25.0)
        view.translateY = 25.0 / 2
        children.add(view)
        return this
    }

    fun append(message: String, color: Color): FXGLTextFlow {
        return append(message, color, 22.0)
    }

    fun append(message: String, color: Color, fontSize: Double): FXGLTextFlow {
        val text = FXGL.getUIFactory().newText(message, color, fontSize)
        children.add(text)
        return this
    }

//    private class KeyView(keyCode: KeyCode, color: Color) : StackPane() {
//
//        private val background = Rectangle(20.0, 24.0, Color.BLACK)
//        private val text = FXGL.getUIFactory().newText(keyCode.getName(), color, 22.0)
//
//        init {
//            background.stroke = Color.BLACK
//            background.strokeWidth = 0.5
//
//            val border = Rectangle(22.0, 26.0, null)
//            border.arcWidth = 5.0
//            border.arcHeight = 5.0
//            border.stroke = color
//            border.strokeWidth = 2.0
//
//            children.addAll(background, border, text)
//        }
//    }
//
//    private class ButtonView(button: MouseButton, color: Color, size: Double) : Pane() {
//
//        init {
//            val border = Rectangle(size, size * 1.5)
//            border.fill = Color.BLACK
//            border.stroke = color
//            border.strokeWidth = size / 7
//            border.arcWidth = size / 1.5
//            border.arcHeight = size / 1.5
//
//            val borderTop = Rectangle(size, size * 1.5)
//            borderTop.fill = null
//            borderTop.stroke = color
//            borderTop.strokeWidth = size / 7
//            borderTop.arcWidth = size / 1.5
//            borderTop.arcHeight = size / 1.5
//
//            val line1 = Line(size / 2, 0.0, size / 2, size / 5)
//            line1.stroke = color
//            line1.strokeWidth = size / 7
//
//            val ellipse = Rectangle(size / 6, size / 6 * 1.5)
//            ellipse.fill = null
//            ellipse.stroke = color
//            ellipse.strokeWidth = size / 10
//            ellipse.arcWidth = size / 1.5
//            ellipse.arcHeight = size / 1.5
//            ellipse.translateX = size / 2 - size / 6 / 2
//            ellipse.translateY = size / 5
//
//            val line2 = Line(size / 2, size / 5 * 2.75, size / 2, size / 5 * 5)
//            line2.stroke = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, color), Stop(0.75, Color.BLACK))
//            line2.strokeWidth = size / 7
//
//            children.addAll(border, line1, line2, ellipse, borderTop)
//
//            when(button) {
//                MouseButton.PRIMARY -> {
//                    val highlight = Rectangle(size / 2.5, size / 6 * 3.5)
//                    highlight.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, Color.BLACK), Stop(0.25, color), Stop(0.8, color), Stop(0.9, Color.BLACK))
//                    highlight.arcWidth = size / 4
//                    highlight.arcHeight = size / 4
//                    highlight.translateX = size / 20
//                    highlight.translateY = size / 8
//
//                    children.add(1, highlight)
//                }
//
//                MouseButton.SECONDARY -> {
//                    val highlight = Rectangle(size / 2.5, size / 6 * 3.5)
//                    highlight.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, Color.BLACK), Stop(0.25, color), Stop(0.8, color), Stop(0.9, Color.BLACK))
//                    highlight.arcWidth = size / 4
//                    highlight.arcHeight = size / 4
//                    highlight.translateX = size - size / 20 - highlight.width
//                    highlight.translateY = size / 8
//
//                    children.add(1, highlight)
//                }
//
//                MouseButton.MIDDLE -> TODO()
//
//                else -> {}
//            }
//        }
//    }
}