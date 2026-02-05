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
 * Based on FPS, by default, time per frame (tpf) is computed for the next 2 seconds.
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

        /**
         * The duration for which the last calculated fps is fixed, i.e. sampling rate.
         */
        private val fpsRefreshRate: Duration = Duration.millis(2000.0),

        private val runnable: (Double) -> Unit) {

    private val log = Logger.get<LoopRunner>()

    /**
     * Number of processed frames per second.
     */
    @get:JvmName("getFPS")
    var fps = 60
        private set

    /**
     * Time difference between this and last frame in seconds.
     */
    @get:JvmName("tpf")
    var tpf = 1.0 / 60
        private set

    var cpuNanoTime = 0L
        private set

    /**
     * Time recorded in the frame where we calculated and then fixed FPS for [fpsRefreshRate].
     */
    private var lastFPSUpdateNanos = 0L

    /**
     * Number of frames since the last frame where we calculated and then fixed FPS for [fpsRefreshRate].
     */
    private var numFramesSinceLastRefresh = 0

    /**
     * Time recorded in last frame, in nanoseconds.
     */
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

    private fun frame(thisFrameNanos: Long) {
        // if this is our first frame since engine started,
        // or first frame since engine resumed
        // then assume time has passed equivalent to the engine running at 60 fps
        // it will get overridden after refresh rate duration has passed
        if (lastFrameNanos == 0L) {
            lastFrameNanos = thisFrameNanos - (1_000_000_000.0 / 60).toLong()
            lastFPSUpdateNanos = lastFrameNanos
            numFramesSinceLastRefresh = 1
        }

        // convert time between frames from nanos to seconds
        tpf = (thisFrameNanos - lastFrameNanos) / 1_000_000_000.0

        // The "executor" will call X times per second even if the game runs under X fps.
        // If it's not even "half" a tick long, skip
        // but only if we are not using the JavaFX loop runner, i.e. ticksPerSecond > 0
        if (ticksPerSecond > 0) {
            if (tpf < 1 / (ticksPerSecond * 1.5)) {
                return
            }
        }

        numFramesSinceLastRefresh++

        // Update the FPS value based on provided refresh rate
        val timeSinceLastFPSUpdateNanos = thisFrameNanos - lastFPSUpdateNanos
        if (timeSinceLastFPSUpdateNanos >= fpsRefreshRate.toNanos()) {
            lastFPSUpdateNanos = thisFrameNanos

            // numFramesSinceLastRefresh -- timeSinceLastFPSUpdateNanos
            // fps                       -- 1_000_000_000L
            // hence, fps equals below
            fps = (numFramesSinceLastRefresh * 1_000_000_000L / timeSinceLastFPSUpdateNanos).toInt()
            numFramesSinceLastRefresh = 0
        }

        lastFrameNanos = thisFrameNanos

        cpuNanoTime = measureNanoTime {
            runnable(tpf)
        }
    }

    private fun Duration.toNanos(): Long {
        return (this.toMillis() * 1_000_000).toLong()
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