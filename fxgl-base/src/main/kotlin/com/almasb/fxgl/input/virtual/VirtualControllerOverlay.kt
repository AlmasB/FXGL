/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.virtual

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGL.Companion.getInput
import com.almasb.fxgl.app.FXGL.Companion.getUIFactory
import com.almasb.fxgl.input.virtual.VirtualButton.*
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon

/**
 * TODO:
 * 1. QTE events can't use virtual controller
 * 2. Trigger Views should have common base code
 * 3. Virtual controller buttons can't be rebound in menu
 * 4. provide a group for dpad and a group for buttons
 * 5. implement remaining controller styles
 * 6. PS and other styles have different layout and buttons, e.g. Square Triangle ...
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class VirtualControllerOverlay(val style: VirtualControllerStyle) : Parent() {

    init {
        val offset = 45.0

        val up = makeButtonDpad(UP)
        val down = makeButtonDpad(DOWN)
        val left = makeButtonDpad(LEFT)
        val right = makeButtonDpad(RIGHT)

        val dpadCenter = Point2D(100 -45.0, 100.0)

        up.translateX = dpadCenter.x
        up.translateY = dpadCenter.y - offset

        down.translateX = dpadCenter.x
        down.translateY = dpadCenter.y + offset

        left.translateX = dpadCenter.x - offset
        left.translateY = dpadCenter.y

        right.translateX = dpadCenter.x + offset
        right.translateY = dpadCenter.y

        val buttonA = makeButton(A, Color.GREEN)
        val buttonB = makeButton(B, Color.RED)
        val buttonX = makeButton(X, Color.BLUE)
        val buttonY = makeButton(Y, Color.YELLOW)

        val buttonsCenter = Point2D(FXGL.getAppWidth() - 100.0, 100.0)

        buttonA.translateX = buttonsCenter.x
        buttonA.translateY = buttonsCenter.y + offset

        buttonB.translateX = buttonsCenter.x + offset
        buttonB.translateY = buttonsCenter.y

        buttonX.translateX = buttonsCenter.x - offset
        buttonX.translateY = buttonsCenter.y

        buttonY.translateX = buttonsCenter.x
        buttonY.translateY = buttonsCenter.y - offset

        children.addAll(up, down, left, right, buttonA, buttonB, buttonX, buttonY)

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
            getInput().pressVirtual(virtualButton)
        }

        root.setOnMouseReleased {
            getInput().releaseVirtual(virtualButton)
        }

        return root
    }

    private fun makeButton(virtualButton: VirtualButton, color: Color): Node {
        val root = StackPane()

        val bg0 = Circle(25.0, color.darker())
        val bg1 = Circle(18.0, Color.TRANSPARENT)

        val text = getUIFactory().newText(virtualButton.toString(), Color.WHITE, 26.0)

        root.children.addAll(bg0, bg1, text)

        bg1.fillProperty().bind(
                Bindings.`when`(root.pressedProperty()).then(color).otherwise(Color.TRANSPARENT)
        )

        root.setOnMousePressed {
            getInput().pressVirtual(virtualButton)
        }

        root.setOnMouseReleased {
            getInput().releaseVirtual(virtualButton)
        }

        return root
    }
}