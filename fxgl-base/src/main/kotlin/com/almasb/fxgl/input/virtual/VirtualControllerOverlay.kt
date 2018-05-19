/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.virtual

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGL.Companion.getInput
import com.almasb.fxgl.app.FXGL.Companion.getUIFactory
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class VirtualControllerOverlay(val style: VirtualControllerStyle) : Parent() {

    init {
        // TODO: movement buttons

        val buttonA = makeButton(VirtualButton.A, Color.GREEN)
        val buttonB = makeButton(VirtualButton.B, Color.RED)
        val buttonX = makeButton(VirtualButton.X, Color.BLUE)
        val buttonY = makeButton(VirtualButton.Y, Color.YELLOW)

        val buttonsCenter = Point2D(FXGL.getAppWidth() - 100.0, 100.0)
        val offset = 45.0

        buttonA.translateX = buttonsCenter.x
        buttonA.translateY = buttonsCenter.y + offset

        buttonB.translateX = buttonsCenter.x + offset
        buttonB.translateY = buttonsCenter.y

        buttonX.translateX = buttonsCenter.x - offset
        buttonX.translateY = buttonsCenter.y

        buttonY.translateX = buttonsCenter.x
        buttonY.translateY = buttonsCenter.y - offset

        children.addAll(buttonA, buttonB, buttonX, buttonY)
    }

    private fun makeButton(virtualButton: VirtualButton, color: Color): Node {
        val root = StackPane()

        val bg0 = Circle(25.0, color.darker())
        val bg1 = Circle(18.0, Color.TRANSPARENT)

        val text = getUIFactory().newText(virtualButton.toString(), Color.WHITE, 26.0)

        root.children.addAll(bg0, bg1, text)

        root.opacity = 0.7

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