/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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