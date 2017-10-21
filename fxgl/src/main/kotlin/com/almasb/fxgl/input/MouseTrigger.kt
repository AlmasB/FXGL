/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.MouseButton

data class MouseTrigger(val button: MouseButton, private val modifier: InputModifier) : Trigger {

    companion object {
        fun buttonFromString(value: String) = when(value) {
            "LMB" -> MouseButton.PRIMARY
            "MMB" -> MouseButton.MIDDLE
            "RMB" -> MouseButton.SECONDARY
            else -> MouseButton.NONE
        }
    }

    constructor(button: MouseButton) : this(button, InputModifier.NONE)

    override fun getModifier() = modifier

    override fun getName() = buttonToString()

    override fun isKey() = false

    override fun isButton() = true

    override fun toString() = (if (modifier == InputModifier.NONE) "" else "$modifier+") + buttonToString()

    private fun buttonToString() = when(button) {
        MouseButton.PRIMARY -> "LMB"
        MouseButton.MIDDLE -> "MMB"
        MouseButton.SECONDARY -> "RMB"
        else -> "NONE"
    }
}