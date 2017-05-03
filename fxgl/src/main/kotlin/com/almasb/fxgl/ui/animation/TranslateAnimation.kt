/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.ui.animation

import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.listener.StateListener
import com.almasb.fxgl.core.math.FXGLMath
import javafx.animation.Interpolator
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TranslateAnimation(val node: Node, val duration: Duration,
                         val start: Point2D, val end: Point2D) : StateListener {

    var onFinished: Runnable? = null

    private var time = 0.0
    private var endTime = duration.toSeconds()

    private var count = 0
    private var cycleCount = 1

    var finished = false
        private set

    var animating = false
        private set

    private lateinit var state: State

    fun start(state: State) {
        this.state = state

        reset()
        animating = true

        state.addStateListener(this)
    }

    fun stop() {
        animating = false
    }

    override fun onUpdate(tpf: Double) {

        if (!animating)
            return

        if (finished)
            return

//        if (checkDelay) {
//            time += tpf
//
//            if (time >= delay.toSeconds()) {
//                checkDelay = false
//                time = 0.0
//            } else {
//                return
//            }
//        }

        if (time == 0.0) {
            //onCycleStarted()

            onProgress(0.0)

            time += tpf
            return
        }

        time += tpf

        if (time >= endTime) {
            //onCycleFinished()

            onProgress(1.0)

            count++

            if (count >= cycleCount) {
                finished = true
                animating = false

                state.removeStateListener(this)

                onFinished?.run()
            } else {
                time = 0.0
            }

            return
        }

        onProgress(time / endTime)
    }

    fun reset() {
        time = 0.0
        finished = false
    }

    private fun onProgress(progress: Double) {
        val i = FXGLMath.interpolate(start, end, progress, Interpolator.LINEAR)

        node.translateX = i.x
        node.translateY = i.y
    }
}