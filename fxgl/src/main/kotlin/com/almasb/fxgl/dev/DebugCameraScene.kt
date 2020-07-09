/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getAppHeight
import com.almasb.fxgl.dsl.getAppWidth
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.scene.SubScene
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DebugCameraScene : SubScene() {

    override val isAllowConcurrency: Boolean
        get() = true

    // negative because we want to bind to these values directly
    // for example, as the camera moves right, the root layout moves left
    private val negativeCameraX = SimpleDoubleProperty()
    private val negativeCameraY = SimpleDoubleProperty()

    init {
        input.addAction(object : UserAction("Right") {
            override fun onAction() {
                negativeCameraX.value -= 5
            }
        }, KeyCode.RIGHT, InputModifier.CTRL)

        input.addAction(object : UserAction("Left") {
            override fun onAction() {
                negativeCameraX.value += 5
            }
        }, KeyCode.LEFT, InputModifier.CTRL)

        input.addAction(object : UserAction("Up") {
            override fun onAction() {
                negativeCameraY.value += 5
            }
        }, KeyCode.UP, InputModifier.CTRL)

        input.addAction(object : UserAction("Down") {
            override fun onAction() {
                negativeCameraY.value -= 5
            }
        }, KeyCode.DOWN, InputModifier.CTRL)

        val viewportBoundary = Rectangle(getAppWidth().toDouble(), getAppHeight().toDouble(), null)
        viewportBoundary.stroke = Color.RED
        viewportBoundary.strokeWidth = 4.0

        contentRoot.children += viewportBoundary
    }

    override fun onCreate() {
        FXGL.getWindowService().window.currentFXGLScene.root.translateXProperty().bind(negativeCameraX)
        FXGL.getWindowService().window.currentFXGLScene.root.translateYProperty().bind(negativeCameraY)
    }

    override fun onDestroy() {
        negativeCameraX.value = 0.0
        negativeCameraY.value = 0.0

        FXGL.getWindowService().window.currentFXGLScene.root.translateXProperty().unbind()
        FXGL.getWindowService().window.currentFXGLScene.root.translateYProperty().unbind()
    }
}