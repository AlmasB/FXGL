/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev.profiling

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.ui.MDIWindow
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ProfilerWindow(width: Double, height: Double, title: String) : MDIWindow() {

    private val log = Logger.get(javaClass)

    private val g: GraphicsContext

    var measuringUnitsName = ""

    var numYTicks = 10

    /**
     * Number of seconds this chart is tracking.
     */
    var durationX = 3.0

    var maxBufferSize = 180

    private val valueBuffer = LinkedList<Double>()

    /**
     * If current value is less than [preferredMaxValue] for 300 updates,
     * then set maxValue to [preferredMaxValue].
     */
    var preferredMaxValue = 0.0

    private var preferredUpdateCounter = 0

    var minValue = 0.0
    var curValue = 0.0
    var maxValue = 0.0

    var bgColor = Color.color(0.25, 0.25, 0.25, 0.75)

    var textColor = Color.WHITESMOKE

    var chartColor = Color.RED

    /* For logging */
    private var totalValue = 0.0
    private var frames = 1
    private var allTimeLow = Double.MAX_VALUE
    private var allTimeHigh = 0.0

    init {
        isCloseable = false
        isMinimizable = true
        isMovable = true
        isManuallyResizable = false

        setPrefSize(width, height)
        this.title = title

        val canvas = Canvas(width, height)
        g = canvas.graphicsContext2D

        contentPane.children += canvas
    }

    fun update(value: Double) {
        frames++
        totalValue += value

        valueBuffer.addLast(value)
        if (valueBuffer.size > maxBufferSize)
            valueBuffer.removeFirst()

        if (value > maxValue)
            maxValue = value

        if (value < minValue)
            minValue = value

        if (value > allTimeHigh)
            allTimeHigh = value

        if (value < allTimeLow)
            allTimeLow = value

        if (maxValue > preferredMaxValue && value < preferredMaxValue) {
            preferredUpdateCounter++

            if (preferredUpdateCounter == 300) {
                maxValue = preferredMaxValue
                preferredUpdateCounter = 0
            }
        }

        g.fill = bgColor
        g.fillRect(0.0, 0.0, g.canvas.width, g.canvas.height)
        g.fill = textColor

        g.stroke = Color.web("darkgray", 0.3)
        g.lineWidth = 1.0

        val tickLength = (maxValue - minValue) * 1.0 / numYTicks

        for (i in numYTicks downTo 0) {
            val tickValue = minValue + i*tickLength

            val y = FXGLMath.map(i.toDouble(), numYTicks.toDouble(), 0.0, 15.0, g.canvas.height - 5)

            g.fillText("%.2f".format(tickValue), 0.0, y)
            g.strokeLine(0.0, y, g.canvas.width, y)
        }

        // render values
        g.stroke = chartColor
        g.lineWidth = 2.0

        val dx = (g.canvas.width - 30) / maxBufferSize

        var x = 30.0
        var lastX = 0.0
        var lastY = 0.0

        valueBuffer.forEach {
            x += dx
            val y = FXGLMath.map(it, maxValue, minValue, 15.0, g.canvas.height - 5)

            if (lastX != 0.0 && lastY != 0.0) {
                g.strokeLine(lastX, lastY, x, y)
            }

            lastX = x
            lastY = y
        }
    }

    fun log() {
        log.info("Profiler ($title) - $frames frames")
        log.info("Average: ${totalValue / frames}")
        log.info("Minimum: $allTimeLow")
        log.info("Maximum: $allTimeHigh")
    }
}

//    // the debug data max chars is ~110, so just add a margin
//    // cache string builder to avoid object allocation
//    private val sb = StringBuilder(128)
//
//    private fun buildInfoText(fps: Int): String {
//        // first clear the contents
//        sb.setLength(0)
//        sb.append("FPS: ").append(fps)
//            .append("\nAvg CPU (ms): ").append("%.1f".format(currentTimeTook / 1_000_000.0))
//            .append("\nNow Mem (MB): ").append(currentMemoryUsageRounded)
//            .append("\nAvg Mem (MB): ").append(avgMemoryUsageRounded)
//            .append("\nMin Mem (MB): ").append(minMemoryUsageRounded)
//            .append("\nMax Mem (MB): ").append(maxMemoryUsageRounded)
//
//        return sb.toString()
//    }