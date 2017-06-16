/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

/**
 * A key modifier applied to an input event.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
enum class InputModifier {

    /**
     * Ctrl key
     */
    CTRL,

    /**
     * Shift key
     */
    SHIFT,

    /**
     * Alt key
     */
    ALT,

    /**
     * No modifier key
     */
    NONE;

    companion object {
        @JvmStatic fun from(event: KeyEvent): InputModifier {
            if (event.isControlDown)
                return CTRL
            if (event.isShiftDown)
                return SHIFT
            if (event.isAltDown)
                return ALT

            return NONE
        }

        @JvmStatic fun from(event: MouseEvent): InputModifier {
            if (event.isControlDown)
                return CTRL
            if (event.isShiftDown)
                return SHIFT
            if (event.isAltDown)
                return ALT

            return NONE
        }
    }

    fun isTriggered(event: KeyEvent): Boolean {
        when (this) {
            CTRL -> return event.isControlDown
            SHIFT -> return event.isShiftDown
            ALT -> return event.isAltDown
            else -> return !(event.isAltDown || event.isShiftDown || event.isControlDown)
        }
    }

    fun isTriggered(event: MouseEvent): Boolean {
        when (this) {
            CTRL -> return event.isControlDown
            SHIFT -> return event.isShiftDown
            ALT -> return event.isAltDown
            else -> return !(event.isAltDown || event.isShiftDown || event.isControlDown)
        }
    }
}