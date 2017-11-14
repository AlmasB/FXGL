/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import java.util.*

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

    fun reset() {
        Arrays.fill(frameTimes, 0)
        index = 0
        arrayFilled = false
        frameRate = 0
    }
}