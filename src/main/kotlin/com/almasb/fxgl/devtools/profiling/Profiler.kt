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

package com.almasb.fxgl.devtools.profiling

import com.almasb.fxeventbus.Subscriber
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.time.UpdateEvent
import com.almasb.fxgl.logging.SystemLogger
import com.almasb.fxgl.time.MasterTimer

/**
 * Basic profiler.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Profiler {

    companion object {
        private val masterTimer: MasterTimer
        private val runtime: Runtime

        private val MB = 1024.0 * 1024.0

        init {
            masterTimer = FXGL.getMasterTimer()
            runtime = Runtime.getRuntime()
        }
    }

    // set to 1 to avoid div by 0
    var frames = 1
        private set

    private var fps = 0.0

    fun getAvgFPS() = fps / frames

    private var performance = 0.0

    fun getAvgPerformance() = performance / frames

    private var memoryUsage = 0L
    private var memoryUsageMin = Long.MAX_VALUE
    private var memoryUsageMax = 0L
    private var memoryUsageCurrent = 0L;

    /**
     * @return average memory usage in MB
     */
    fun getAvgMemoryUsage() = memoryUsage / frames / MB

    /**
     * @return max (highest peak) memory usage in MB
     */
    fun getMaxMemoryUsage() = memoryUsageMax / MB

    /**
     * @return min (lowest peak) memory usage in MB
     */
    fun getMinMemoryUsage() = memoryUsageMin / MB

    /**
     * @return how much memory is used at this moment in MB
     */
    fun getCurrentMemoryUsage() = memoryUsageCurrent / MB

    private var subscription: Subscriber? = null

    private fun onUpdateEvent(event: UpdateEvent) {
        frames++
        fps += masterTimer.fps
        performance += masterTimer.performanceFPS

        memoryUsageCurrent = runtime.totalMemory() - runtime.freeMemory()
        memoryUsage += memoryUsageCurrent

        if (memoryUsageCurrent > memoryUsageMax)
            memoryUsageMax = memoryUsageCurrent

        if (memoryUsageCurrent < memoryUsageMin)
            memoryUsageMin = memoryUsageCurrent
    }

    /**
     * Starts profiling FPS values.
     */
    fun start() {
        if (subscription == null)
            subscription = FXGL.getEventBus().addEventHandler(UpdateEvent.ANY, { onUpdateEvent(it) })
    }

    /**
     * Stops profiling FPS values.
     */
    fun stop() {
        subscription?.unsubscribe()
    }

    /**
     * Resets values to default.
     */
    fun reset() {
        frames = 1
        fps = 0.0
        performance = 0.0

        memoryUsage = 0L
        memoryUsageMin = Long.MAX_VALUE
        memoryUsageMax = 0L
        memoryUsageCurrent = 0L
    }

    /**
     * Print profiles values to SystemLogger.
     */
    fun print() {
        SystemLogger.info("Processed Frames: $frames")
        SystemLogger.info("Average FPS: ${getAvgFPS()}")
        SystemLogger.info("Average Performance: ${getAvgPerformance()}")
        SystemLogger.info("Average Memory Usage: ${getAvgMemoryUsage()} MB")
        SystemLogger.info("Min Memory Usage: ${getMinMemoryUsage()} MB")
        SystemLogger.info("Max Memory Usage: ${getMaxMemoryUsage()} MB")
    }
}