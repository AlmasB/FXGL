/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.*
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
    fun isTriggered(event: InputEvent): Boolean
    fun isReleased(event: InputEvent): Boolean
}

data class KeyTrigger
@JvmOverloads constructor(val key: KeyCode,
                          private val modifier: InputModifier = InputModifier.NONE) : Trigger {

    override fun getModifier() = modifier

    override fun getName() = key.getName()

    override fun isKey() = true
    override fun isButton() = false

    override fun isTriggered(event: InputEvent): Boolean {
        if (event !is KeyEvent)
            return false

        return event.code == key && modifier.isTriggered(event)
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun isReleased(event: InputEvent): Boolean {
        if (event !is KeyEvent)
            return false

        when (event.code) {
            KeyCode.CONTROL -> return modifier == InputModifier.CTRL
            KeyCode.SHIFT -> return modifier == InputModifier.SHIFT
            KeyCode.ALT -> return modifier == InputModifier.ALT
        }

        return event.code == key
    }

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

    override fun isTriggered(event: InputEvent): Boolean {
        if (event !is MouseEvent)
            return false

        return event.button == button && modifier.isTriggered(event)
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun isReleased(event: InputEvent): Boolean {
        if (event is KeyEvent) {
            when (event.code) {
                KeyCode.CONTROL -> return modifier == InputModifier.CTRL
                KeyCode.SHIFT -> return modifier == InputModifier.SHIFT
                KeyCode.ALT -> return modifier == InputModifier.ALT
            }
        }

        return isTriggered(event)
    }

    override fun toString() = (if (modifier == InputModifier.NONE) "" else "$modifier+") + buttonToString()

    private fun buttonToString() = when(button) {
        MouseButton.PRIMARY -> "LMB"
        MouseButton.MIDDLE -> "MMB"
        MouseButton.SECONDARY -> "RMB"
        else -> throw RuntimeException("Not a mouse button")
    }
}