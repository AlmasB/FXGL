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

package com.almasb.fxgl.time

import com.google.inject.Inject
import javafx.util.Duration

/**
 * Simple timer to capture current time and check if certain time has passed.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLLocalTimer
@Inject
private constructor(private val masterTimer: com.almasb.fxgl.time.MasterTimer) : com.almasb.fxgl.time.LocalTimer {
    private var time: Long = 0

    /**
     * Captures current time.
     */
    override fun capture() {
        time = masterTimer.now
    }

    /**
     * Returns true if difference between captured time
     * and now is greater or equal to given duration.

     * @param duration time duration to check
     * *
     * @return true if elapsed, false otherwise
     */
    override fun elapsed(duration: Duration) =
            masterTimer.now - time >= FXGLMasterTimer.toNanos(duration)
}