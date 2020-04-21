package com.almasb.fxgl.input.virtual.joystick

import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

/**
 * Default virtual joystick.
 */
class FXGLVirtualJoystick(override val outerCircle: Circle, override val innerCircle: Circle) : Parent(), VirtualJoystick {
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

    override fun addInnerCircleListener() {
        TODO("Not yet implemented")
    }

    private fun showCircles() {
        children.addAll(outerCircle, innerCircle)
    }
}