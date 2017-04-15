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

package com.almasb.fxgl.service.impl.timer

/**
 * Convenience class that buffers FPS values and calculates
 * average FPS.
 *
 * Adapted from http://wecode4fun.blogspot.co.uk/2015/07/particles.html (Roland C.)
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
internal class FPSCounter {

    companion object {
        private val MAX_SAMPLES = 100
    }

    private val frameTimes = LongArray(MAX_SAMPLES)
    private var index = 0
    private var arrayFilled = false
    private var frameRate = 0

    fun update(now: Long): Int {
        val oldFrameTime = frameTimes[index]
        frameTimes[index] = now
        index = (index + 1) % frameTimes.size

        if (index == 0) {
            arrayFilled = true
        }

        if (arrayFilled) {
            val elapsedNanos = now - oldFrameTime
            val elapsedNanosPerFrame = elapsedNanos / frameTimes.size
            frameRate = (1_000_000_000.0 / elapsedNanosPerFrame).toInt()
        }

        return frameRate
    }
}