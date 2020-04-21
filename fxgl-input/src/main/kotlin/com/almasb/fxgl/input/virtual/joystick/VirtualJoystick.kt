package com.almasb.fxgl.input.virtual.joystick

import javafx.scene.shape.Circle

interface VirtualJoystick {
    val outerCircle: Circle
    val innerCircle: Circle

    fun addInnerCircleListener()
    fun reset()
}