/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.logging.Logger
import javafx.animation.AnimationTimer
import javafx.application.Platform
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
    private var fpsBuffer2sec = 0

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

        impl.resume()
    }

    fun pause() {
        log.debug("Pausing loop")

        impl.pause()

        lastFPSUpdateNanos = 0L
    }

    fun stop() {
        log.debug("Stopping loop")

        impl.stop()
    }

    private fun frame(now: Long) {
        if (lastFPSUpdateNanos == 0L) {
            lastFPSUpdateNanos = now
            fpsBuffer2sec = 0
        }

        cpuNanoTime = measureNanoTime {
            runnable(tpf)
        }

        fpsBuffer2sec++

        // if 2 seconds have passed
        if (now - lastFPSUpdateNanos >= 2_000_000_000) {
            lastFPSUpdateNanos = now
            fps = fpsBuffer2sec / 2
            fpsBuffer2sec = 0

            // tweak potentially erroneous reads
            if (fps < 5)
                fps = 60

            // update tpf for the next 2 seconds
            tpf = 1.0 / fps
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