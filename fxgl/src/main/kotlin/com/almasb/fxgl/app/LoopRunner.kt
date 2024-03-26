/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.logging.Logger
import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.util.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime

/**
 * The main loop runner.
 * Uses the number of JavaFX pulse calls per second (using a 2-sec buffer) to compute FPS.
 * Based on FPS, time per frame (tpf) is computed for the next 2 seconds.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class LoopRunner(

        /**
         * The number of ticks in one second.
         * Any negative value or 0 means that the runner will match number of ticks
         * to the display refresh rate.
         */
        private val ticksPerSecond: Int = -1,

        private val fpsRefreshRate: Duration = Duration.millis(500.0),

        private val runnable: (Double) -> Unit) {

    private val log = Logger.get<LoopRunner>()

    // use 60 as default until fps buffer is full
    @get:JvmName("getFPS")
    var fps = 60
        private set

    // use 1.0 / 60 as default until fps buffer is full
    @get:JvmName("tpf")
    var tpf = 1.0 / 60
        private set

    var cpuNanoTime = 0L
        private set

    private var lastFPSUpdateNanos = 0L
    private var fpsSamplingCount = 0
    private var lastFrameNanos = 0L

    private val impl by lazy {
        if (ticksPerSecond <= 0) {
            log.debug("Initializing JavaFX AnimationTimerLoop")
            object : AnimationTimerLoop() {
                override fun onTick(now: Long) {
                    frame(now)
                }
            }
        } else {
            log.debug("Initializing ScheduledExecutorLoop with fps: $ticksPerSecond")
            object : ScheduledExecutorLoop(ticksPerSecond) {
                override fun onTick(now: Long) {
                    frame(now)
                }
            }
        }
    }

    fun start() {
        log.debug("Starting loop")

        impl.start()
    }

    fun resume() {
        log.debug("Resuming loop")

        lastFrameNanos = 0
        impl.resume()
    }

    fun pause() {
        log.debug("Pausing loop")

        impl.pause()
    }

    fun stop() {
        log.debug("Stopping loop")

        impl.stop()
    }

    private fun frame(now: Long) {
        val ticksPerSecond = if (ticksPerSecond < 0) 60 else ticksPerSecond // When unknown, default to 60 fps

        if (lastFrameNanos == 0L) {
            lastFrameNanos = now - (1_000_000_000.0 / ticksPerSecond).toLong()
            lastFPSUpdateNanos = lastFrameNanos
            fpsSamplingCount = 1
        }

        tpf = (now - lastFrameNanos).toDouble() / 1_000_000_000

        // The "executor" will call 60 times per seconds even if the game runs under 60 fps.
        // If it's not even "half" a tick long, skip
        if(tpf < (1_000_000_000 / (ticksPerSecond * 1.5)) / 1_000_000_000 ) {
            return
        }

        fpsSamplingCount++

        // Update the FPS value based on provided refresh rate
        val timeSinceLastFPSUpdateNanos = now - lastFPSUpdateNanos;
        if (timeSinceLastFPSUpdateNanos >= fpsRefreshRate.toMillis() * 1_000_000) {
            lastFPSUpdateNanos = now
            fps = (fpsSamplingCount.toLong() * 1_000_000_000 / timeSinceLastFPSUpdateNanos).toInt()
            fpsSamplingCount = 0
        }

        lastFrameNanos = now

        cpuNanoTime = measureNanoTime {
            runnable(tpf)
        }
    }
}

private interface Loop {
    fun start()
    fun pause()
    fun resume()
    fun stop()

    fun onTick(now: Long)
}

private abstract class AnimationTimerLoop : Loop {

    private val timer = object : AnimationTimer() {
        override fun handle(now: Long) {
            onTick(now)
        }
    }

    override fun start() {
        timer.start()
    }

    override fun pause() {
        timer.stop()
    }

    override fun resume() {
        timer.start()
    }

    override fun stop() {
        timer.stop()
    }
}

private abstract class ScheduledExecutorLoop(private val ticksPerSecond: Int) : Loop {
    private var isPaused = false

    private val executor = Executors.newSingleThreadScheduledExecutor()

    override fun start() {
        // nanoseconds per tick
        val period = (1_000_000_000.0 / ticksPerSecond).toLong()

        executor.scheduleAtFixedRate({
            if (!isPaused) {
                Platform.runLater {
                    onTick(System.nanoTime())
                }
            }

        }, 0, period, TimeUnit.NANOSECONDS)
    }

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
        isPaused = false
    }

    override fun stop() {
        executor.shutdownNow()
    }
}