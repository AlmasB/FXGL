/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.time.FPSCounter
import com.almasb.fxgl.core.util.Consumer
import javafx.animation.AnimationTimer

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class LoopRunner(val runnable: Consumer<Double>) {

    private val log = Logger.get<LoopRunner>()

    @get:JvmName("getFPS")
    var fps = 0
        private set

    @get:JvmName("tpf")
    var tpf = 0.0
        private set

    private val fpsCounter = FPSCounter()

    private val impl by lazy {
        object : AnimationTimer() {

            override fun handle(now: Long) {
                tpf = tpfCompute(now)

                frame()
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
        fpsCounter.reset()
    }

    fun stop() {
        log.debug("Stopping loop")

        impl.stop()
    }

    private fun tpfCompute(now: Long): Double {
        fps = (fpsCounter.update(now))

        // assume that fps is at least 5 to avoid subtle bugs
        // disregard minor fluctuations > 55 for smoother experience
        if (fps < 5 || fps > 55)
            fps = (60)

        return 1.0 / fps
    }

    private fun frame() {
        runnable.accept(tpf)
    }
}