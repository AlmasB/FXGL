/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.virtual

import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.virtual.VirtualButton.*
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class VirtualControllerOverlay(private val input: Input, val style: VirtualControllerStyle) : Parent() {

    val dpad: Node
    val buttons: Node

    init {
        val offset = 45.0

        val up = makeButtonDpad(UP)
        val down = makeButtonDpad(DOWN)
        val left = makeButtonDpad(LEFT)
        val right = makeButtonDpad(RIGHT)

        val dpadCenter = Point2D(100 - 45.0, 100.0)

        up.translateX = dpadCenter.x
        up.translateY = dpadCenter.y - offset

        down.translateX = dpadCenter.x
        down.translateY = dpadCenter.y + offset

        left.translateX = dpadCenter.x - offset
        left.translateY = dpadCenter.y

        right.translateX = dpadCenter.x + offset
        right.translateY = dpadCenter.y

        dpad = Group(up, down, left, right)

        val buttonA = makeButton(A, Color.GREEN)
        val buttonB = makeButton(B, Color.RED)
        val buttonX = makeButton(X, Color.BLUE)
        val buttonY = makeButton(Y, Color.YELLOW)

        val buttonsCenter = Point2D(100.0 - 45.0, 100.0)

        buttonA.translateX = buttonsCenter.x
        buttonA.translateY = buttonsCenter.y + offset

        buttonB.translateX = buttonsCenter.x + offset
        buttonB.translateY = buttonsCenter.y

        buttonX.translateX = buttonsCenter.x - offset
        buttonX.translateY = buttonsCenter.y

        buttonY.translateX = buttonsCenter.x
        buttonY.translateY = buttonsCenter.y - offset

        buttons = Group(buttonA, buttonB, buttonX, buttonY)
        //buttons.translateX = FXGL.getAppWidth() - 155.0

        children.addAll(dpad, buttons)

        opacity = 0.7
    }

    private fun makeButtonDpad(virtualButton: VirtualButton): Node {
        val root = StackPane()

        val bg = Circle(25.0, Color.BLACK)

        val triangle = Polygon(
                20.0, 10.0,
                35.0, 35.0,
                5.0, 35.0
        )

        triangle.fill = Color.WHITE

        triangle.fillProperty().bind(
                Bindings.`when`(root.pressedProperty()).then(Color.GRAY).otherwise(Color.WHITE)
        )

        when (virtualButton) {
            LEFT -> { triangle.rotate = -90.0 }
            RIGHT -> { triangle.rotate = 90.0 }
            UP -> {}
            DOWN -> { triangle.rotate = 180.0 }
            else -> {}
        }

        root.children.addAll(bg, triangle)

        root.setOnMousePressed {
            input.pressVirtual(virtualButton)
        }

        root.setOnMouseReleased {
            input.releaseVirtual(virtualButton)
        }

        return root
    }

    private fun makeButton(virtualButton: VirtualButton, color: Color): Node {
        val root = StackPane()

        val bg0 = Circle(25.0, color.darker())
        val bg1 = Circle(18.0, Color.TRANSPARENT)

        //val text = getUIFactory().newText(virtualButton.toString(), Color.WHITE, 26.0)
        val text = Text(virtualButton.toString())
        text.fill = Color.WHITE
        text.font = Font.font(26.0)
        
        root.children.addAll(bg0, bg1, text)

        bg1.fillProperty().bind(
                Bindings.`when`(root.pressedProperty()).then(color).otherwise(Color.TRANSPARENT)
        )

        root.setOnMousePressed {
            input.pressVirtual(virtualButton)
        }

        root.setOnMouseReleased {
            input.releaseVirtual(virtualButton)
        }

        return root
    }
}