/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import java.lang.RuntimeException

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

interface Trigger {

    fun getModifier(): InputModifier
    fun getName(): String
    fun isKey(): Boolean
    fun isButton(): Boolean
}

data class KeyTrigger
@JvmOverloads constructor(val key: KeyCode,
                          private val modifier: InputModifier = InputModifier.NONE) : Trigger {

    override fun getModifier() = modifier

    override fun getName() = key.getName()

    override fun isKey() = true
    override fun isButton() = false

    override fun toString() = (if (modifier == InputModifier.NONE) "" else "$modifier+") + key.getName()
}

data class MouseTrigger
@JvmOverloads constructor(val button: MouseButton,
                          private val modifier: InputModifier = InputModifier.NONE) : Trigger {

    companion object {
        fun buttonFromString(value: String) = when(value) {
            "LMB" -> MouseButton.PRIMARY
            "MMB" -> MouseButton.MIDDLE
            "RMB" -> MouseButton.SECONDARY
            else -> throw RuntimeException("Must be one of [LMB, MMB, RMB]")
        }
    }

    override fun getModifier() = modifier

    override fun getName() = buttonToString()

    override fun isKey() = false
    override fun isButton() = true

    override fun toString() = (if (modifier == InputModifier.NONE) "" else "$modifier+") + buttonToString()

    private fun buttonToString() = when(button) {
        MouseButton.PRIMARY -> "LMB"
        MouseButton.MIDDLE -> "MMB"
        MouseButton.SECONDARY -> "RMB"
        else -> throw RuntimeException("Not a mouse button")
    }
}