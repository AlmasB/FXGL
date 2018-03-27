/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools.profiling

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.math.FXGLMath
import javafx.scene.text.Text

/**
 * Basic profiler.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Profiler {

    companion object {
        private val runtime = Runtime.getRuntime()

        private val MB = 1024.0f * 1024.0f
    }

    // set to 1 to avoid div by 0
    var frames = 1
        private set

    private var fps = 0
    private var currentFPS = 0
    private var currentTimeTook = 0L

    fun getAvgFPS() = fps / frames

    private var timeTook = 0L

    /**
     * @return time took in nanoseconds
     */
    fun getAvgTimeTook() = timeTook / frames

    /**
     * @return time took in milliseconds
     */
    fun getAvgTimeTookRounded() = "%.2f".format(getAvgTimeTook() / 1_000_000.0)

    private var memoryUsage = 0L
    private var memoryUsageMin = Long.MAX_VALUE
    private var memoryUsageMax = 0L
    private var memoryUsageCurrent = 0L

    /**
     * @return average memory usage in MB
     */
    fun getAvgMemoryUsage() = memoryUsage / frames / MB

    fun getAvgMemoryUsageRounded() = FXGLMath.roundPositive(getAvgMemoryUsage().toDouble())

    /**
     * @return max (highest peak) memory usage in MB
     */
    fun getMaxMemoryUsage() = memoryUsageMax / MB

    fun getMaxMemoryUsageRounded() = FXGLMath.roundPositive(getMaxMemoryUsage().toDouble())

    /**
     * @return min (lowest peak) memory usage in MB
     */
    fun getMinMemoryUsage() = memoryUsageMin / MB

    fun getMinMemoryUsageRounded() = FXGLMath.roundPositive(getMinMemoryUsage().toDouble())

    /**
     * @return how much memory is used at this moment in MB
     */
    fun getCurrentMemoryUsage() = memoryUsageCurrent / MB

    fun getCurrentMemoryUsageRounded() = FXGLMath.roundPositive(getCurrentMemoryUsage().toDouble())

    private var gcRuns = 0

    fun update(fps: Int, timeTook: Long) {
        frames++

        currentFPS = fps
        currentTimeTook = timeTook

        this.fps += fps
        this.timeTook += timeTook

        val used = runtime.totalMemory() - runtime.freeMemory()

        // ignore incorrect readings
        if (used < 0)
            return

        if (used < memoryUsageCurrent) {
            gcRuns++
        }

        memoryUsageCurrent = used
        memoryUsage += memoryUsageCurrent

        if (memoryUsageCurrent > memoryUsageMax)
            memoryUsageMax = memoryUsageCurrent

        if (memoryUsageCurrent < memoryUsageMin)
            memoryUsageMin = memoryUsageCurrent
    }

    fun render(text: Text) {
        text.text = getInfo()
    }

    fun print() {
        val log = Logger.get(javaClass)

        log.info("Processed Frames: $frames")
        log.info("Average FPS: ${getAvgFPS()}")
        log.info("Avg Frame Took: ${getAvgTimeTookRounded()} ms")
        log.info("Avg Memory Usage: ${getAvgMemoryUsageRounded()} MB")
        log.info("Min Memory Usage: ${getMinMemoryUsageRounded()} MB")
        log.info("Max Memory Usage: ${getMaxMemoryUsageRounded()} MB")
        log.info("Estimated GC runs: $gcRuns")
    }

    // the debug data max chars is ~110, so just add a margin
    // cache string builder to avoid object allocation
    private val sb = StringBuilder(128)

    fun getInfo(): String {
        // first clear the contents
        sb.setLength(0)
        sb.append("FPS: ").append(currentFPS)
            .append("\nLast Frame (ms): ").append("%.0f".format(currentTimeTook / 1_000_000.0))
            .append("\nNow Mem (MB): ").append(getCurrentMemoryUsageRounded())
            .append("\nAvg Mem (MB): ").append(getAvgMemoryUsageRounded())
            .append("\nMin Mem (MB): ").append(getMinMemoryUsageRounded())
            .append("\nMax Mem (MB): ").append(getMaxMemoryUsageRounded())

        return sb.toString()
    }
}