/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.gameplay.qte

import javafx.scene.input.KeyCode
import javafx.util.Duration
import java.util.function.Consumer

/**
 * Quick Time Events.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface QTE {

    /**
     * Starts quick time event.
     * Game execution is blocked during the event.
     * The event can be finishes if one of the following conditions is met:
     *
     *  * User runs out of time (fail)
     *  * User presses the wrong key (fail)
     *  * User correctly presses all keys (success)
     *
     * @param callback called with true if user succeeds in the event, false otherwise
     * @param duration how long the event should last
     * @param keys what keys need to be pressed
     */
    fun start(callback: Consumer<Boolean>, duration: Duration, vararg keys: KeyCode)
}