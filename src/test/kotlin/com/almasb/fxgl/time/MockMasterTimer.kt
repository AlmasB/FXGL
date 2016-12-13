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

import com.almasb.fxgl.settings.UserProfile
import javafx.beans.property.*
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object MockMasterTimer : MasterTimer {
    override fun onPause() {
    }

    override fun onResume() {
    }

    override fun save(profile: UserProfile?) {
    }

    override fun onReset() {
    }

    override fun onExit() {
    }

    override fun load(profile: UserProfile?) {
    }

    override fun getNow(): Long {
        return 0
    }

    override fun playtimeProperty(): ReadOnlyLongProperty {
        return SimpleLongProperty()
    }

    override fun tpf(): Double {
        return 0.0
    }

    override fun tickProperty(): ReadOnlyLongProperty {
        return SimpleLongProperty()
    }

    override fun fpsProperty(): IntegerProperty {
        return SimpleIntegerProperty()
    }

    override fun performanceFPSProperty(): IntegerProperty {
        return SimpleIntegerProperty()
    }

    override fun runAtInterval(action: Runnable?, interval: Duration?): TimerAction? {
        return null
    }

    override fun runAtIntervalWhile(action: Runnable?, interval: Duration?,
                                    whileCondition: ReadOnlyBooleanProperty?): TimerAction? {
        return null
    }

    override fun runOnceAfter(action: Runnable?, delay: Duration?): TimerAction? {
        return null
    }

    override fun reset() {
    }

    override fun addUpdateListener(listener: UpdateEventListener?) {
    }

    override fun removeUpdateListener(listener: UpdateEventListener?) {
    }
}