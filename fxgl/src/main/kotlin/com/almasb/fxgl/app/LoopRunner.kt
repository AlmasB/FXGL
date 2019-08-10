/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.sslogger.Logger
import javafx.animation.AnimationTimer

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class LoopRunner(private val runnable: (Double) -> Unit) {

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
        runnable(tpf)
    }

    /**
     * Convenience class that buffers FPS values and calculates
     * average FPS.
     *
     * Adapted from http://wecode4fun.blogspot.co.uk/2015/07/particles.html (Roland C.)
     *
     * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
     */
    private class FPSCounter {

        companion object {
            private const val MAX_SAMPLES = 100
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
            frameTimes.fill(0)
            index = 0
            arrayFilled = false
            frameRate = 0
        }
    }
}