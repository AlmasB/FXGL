/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.scene.input.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * An abstraction for a key or a mouse button.
 */
interface Trigger {

    /**
     * A modifier associated with this trigger.
     */
    val modifier: InputModifier

    /**
     * For keys: W, S, A, D, F, G ...
     * For mouse buttons: one of [LMB, MMB, RMB]
     */
    val name: String

    /**
     * True if this is a key trigger.
     */
    val isKey: Boolean

    /**
     * True if this is a mouse button trigger.
     */
    val isButton: Boolean

    /**
     * Given input [event] that occurred, returns true if this trigger fully
     * (i.e. in CTRL+W, both CTRL modifier and W must match)
     * matches the event's signature.
     */
    fun isTriggered(event: InputEvent): Boolean

    /**
     * Given input [event] that occurred, returns true if this trigger partially
     * (i.e. in CTRL+W, CTRL modifier or W must match)
     * matches the event's signature.
     */
    fun isReleased(event: InputEvent): Boolean
}

/**
 * Wraps [trigger] with an observable trigger object and observable name.
 * This is useful in the case the user rebinds actions to different triggers:
 * we maintain the wrapper object the same and only update its internal data, i.e. trigger and name.
 */
internal class ObservableTrigger(trigger: Trigger) {

    val trigger = ReadOnlyObjectWrapper(trigger)
    val name = ReadOnlyStringWrapper(trigger.toString())

    init {
        this.trigger.addListener { _, _, newTrigger ->
            name.value = newTrigger.toString()
        }
    }

    fun isTriggered(event: InputEvent) = trigger.value.isTriggered(event)
    fun isReleased(event: InputEvent) = trigger.value.isReleased(event)
}

data class KeyTrigger
@JvmOverloads constructor(val key: KeyCode,
                          override val modifier: InputModifier = InputModifier.NONE) : Trigger {

    override val name: String = key.getName()

    override val isKey = true
    override val isButton = false

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
                          override val modifier: InputModifier = InputModifier.NONE) : Trigger {

    companion object {
        fun buttonFromString(value: String) = when(value) {
            "LMB" -> MouseButton.PRIMARY
            "MMB" -> MouseButton.MIDDLE
            "RMB" -> MouseButton.SECONDARY
            else -> throw RuntimeException("Must be one of [LMB, MMB, RMB]")
        }

        fun buttonToString(button: MouseButton) = when(button) {
            MouseButton.PRIMARY -> "LMB"
            MouseButton.MIDDLE -> "MMB"
            MouseButton.SECONDARY -> "RMB"
            else -> throw RuntimeException("Not a mouse button")
        }
    }

    override val name: String = buttonToString(button)

    override val isKey = false
    override val isButton = true

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

    override fun toString() = (if (modifier == InputModifier.NONE) "" else "$modifier+") + name
}