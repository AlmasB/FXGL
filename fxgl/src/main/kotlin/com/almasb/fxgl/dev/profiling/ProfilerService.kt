/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev.profiling

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.ui.FontType
import com.almasb.fxgl.ui.UIFactoryService
import com.almasb.fxgl.logging.Logger
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import java.util.LinkedList
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Basic profiler.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class ProfilerService : EngineService() {

    companion object {
        private val runtime = Runtime.getRuntime()

        private const val MB = 1024.0f * 1024.0f
        private const val TIME_BUFFER_CAPACITY = 120
    }

    private lateinit var sceneService: SceneService
    private lateinit var uiService: UIFactoryService

    // set to 1 to avoid div by 0
    var frames = 1
        private set

    private var fps = 0
    private var currentFPS = 0
    private var currentTimeTook = 0L
    private val timeBuffer = LinkedList<Long>()

    val avgFPS: Int
        get() = fps / frames

    private var timeTook = 0L

    /**
     * @return time took in nanoseconds
     */
    val avgTimeTook: Long
        get() = timeTook / frames

    /**
     * @return time took in milliseconds
     */
    val avgTimeTookRounded: String
        get() = "%.2f".format(avgTimeTook / 1_000_000.0)

    private var memoryUsage = 0L
    private var memoryUsageMin = Long.MAX_VALUE
    private var memoryUsageMax = 0L
    private var memoryUsageCurrent = 0L

    /**
     * @return average memory usage in MB
     */
    val avgMemoryUsage: Float
        get() = memoryUsage / frames / MB

    val avgMemoryUsageRounded: Long
        get() = avgMemoryUsage.roundToLong()

    /**
     * @return max (highest value) memory usage in MB
     */
    val maxMemoryUsage: Float
        get() = memoryUsageMax / MB

    val maxMemoryUsageRounded: Long
        get() = maxMemoryUsage.roundToLong()

    /**
     * @return min (lowest value) memory usage in MB
     */
    val minMemoryUsage: Float
        get() = memoryUsageMin / MB

    val minMemoryUsageRounded: Long
        get() = minMemoryUsage.roundToLong()

    /**
     * @return how much memory is used at this moment in MB
     */
    val currentMemoryUsage: Float
        get() = memoryUsageCurrent / MB

    val currentMemoryUsageRounded: Long
        get() = currentMemoryUsage.roundToLong()

    private var gcRuns = 0

    private var prevNanoTime = 0L

    private lateinit var text: Text

    override fun onInit() {
        text = uiService.newText("", Color.RED, FontType.MONO, 22.0)
        text.text = buildInfoText()
        text.translateY = 25.0

        val bg = Rectangle(text.layoutBounds.width, text.layoutBounds.height, Color.color(0.8, 0.8, 0.8, 0.7))

        val pane = Pane(bg, text)
        pane.isMouseTransparent = true
        pane.translateY = sceneService.appHeight - text.layoutBounds.height

        sceneService.overlayRoot.children += pane
    }

    override fun onUpdate(tpf: Double) {
        if (prevNanoTime == 0L) {
            prevNanoTime = System.nanoTime()
            return
        }

        val fps = (1.0 / tpf).roundToInt()
        val curNanoTime = System.nanoTime()
        val timeTook = curNanoTime - prevNanoTime
        prevNanoTime = curNanoTime

        timeBuffer.addLast(timeTook)
        if (timeBuffer.size > TIME_BUFFER_CAPACITY) timeBuffer.removeFirst()

        update(fps, timeBuffer.average().toLong())

        text.text = buildInfoText()
    }

    private fun update(fps: Int, timeTook: Long) {
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

    // the debug data max chars is ~110, so just add a margin
    // cache string builder to avoid object allocation
    private val sb = StringBuilder(128)

    private fun buildInfoText(): String {
        // first clear the contents
        sb.setLength(0)
        sb.append("FPS: ").append(currentFPS)
            .append("\nLast Frame (ms): ").append("%.1f".format(currentTimeTook / 1_000_000.0))
            .append("\nNow Mem (MB): ").append(currentMemoryUsageRounded)
            .append("\nAvg Mem (MB): ").append(avgMemoryUsageRounded)
            .append("\nMin Mem (MB): ").append(minMemoryUsageRounded)
            .append("\nMax Mem (MB): ").append(maxMemoryUsageRounded)

        return sb.toString()
    }

    override fun onExit() {
        val log = Logger.get(javaClass)

        log.info("Processed Frames: $frames")
        log.info("Average FPS: $avgFPS")
        log.info("Avg Frame Took: $avgTimeTookRounded ms")
        log.info("Avg Memory Usage: $avgMemoryUsageRounded MB")
        log.info("Min Memory Usage: $minMemoryUsageRounded MB")
        log.info("Max Memory Usage: $maxMemoryUsageRounded MB")
        log.info("Estimated GC runs: $gcRuns")
    }
}