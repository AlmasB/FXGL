/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.logging.Logger
import javafx.animation.AnimationTimer
import kotlin.system.measureNanoTime

/**
 * The main loop runner.
 * Uses the number of JavaFX pulse calls per second (using a 2-sec buffer) to compute FPS.
 * Based on FPS, time per frame (tpf) is computed for the next 2 seconds.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class LoopRunner(private val runnable: (Double) -> Unit) {

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
        object : AnimationTimer() {

            override fun handle(now: Long) {
                frame(now)
            }
        }
    }

    var isStarted = false
        private set

    fun start() {
        log.debug("Starting loop")

        require(!isStarted) { "Attempted to start an active loop. Use resume() instead if needed" }

        isStarted = true

        impl.start()
    }

    fun resume() {
        log.debug("Resuming loop")

        impl.start()
    }

    fun pause() {
        log.debug("Pausing loop")

        impl.stop()
    }

    fun stop() {
        log.debug("Stopping loop")

        impl.stop()
    }

    private fun frame(now: Long) {
        if (lastFPSUpdateNanos == 0L) {
            lastFPSUpdateNanos = now
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

            // update tpf for the next 2 seconds
            tpf = 1.0 / fps
        }
    }
}