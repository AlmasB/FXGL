/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.virtual.joystick

import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

/**
 * Default implementation of [VirtualJoystick]. This uses [Circle]s to display the virtual joystick to the player.
 */
class FXGLVirtualJoystick(outerNode: Node, innerNode: Node) : VirtualJoystick(outerNode, innerNode) {
    companion object {
        @JvmStatic
        fun createDefault(): FXGLVirtualJoystick {
            val outerCircle = Circle(0.0, 0.0, 30.0, Color.GREY)
            val innerCircle = Circle(0.0, 0.0, 15.0, Color.LIGHTGRAY)
            return FXGLVirtualJoystick(outerCircle, innerCircle)
        }
    }

    init {
        showCircles()
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    private fun showCircles() {
        children.addAll(outerNode, innerNode)
    }
}