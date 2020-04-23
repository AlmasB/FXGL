/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.virtual.joystick

import javafx.scene.Node
import javafx.scene.Parent

/**
 * A Virtual Joystick allows analog input to be provided by the player, when the hardware is not suitable. This is
 * typically placed on the screen at one of the bottom corners. An example usage of this is to move a character around.
 *
 * A default implementation is available - [FXGLVirtualJoystick]
 */
abstract class VirtualJoystick(val outerNode: Node, val innerNode: Node) : Parent() {

    /**
     * Reset innerNode to the center position.
     */
    abstract fun reset()
}