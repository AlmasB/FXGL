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

package com.almasb.fxgl.entity.animation

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLListener
import javafx.animation.Animation
import javafx.event.EventHandler

/**
 * TODO: use lerp and manual animation. DO NOT rely on JavaFX timeline
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class EntityAnimation(protected val animationBuilder: AnimationBuilder) {

    companion object {
        private val log = FXGL.getLogger(EntityAnimation::class.java)
    }

    protected lateinit var animation: Animation
    private var state = AnimationState.INIT

    private val listener = object : FXGLListener {
        override fun onPause() {
            if (state == AnimationState.PLAYING)
                pause()
        }

        override fun onResume() {
            if (state == AnimationState.PAUSED)
                play()
        }
    }

    protected fun initAnimation() {
        animation = buildAnimation()
        animation.cycleCount = animationBuilder.times
        animation.delay = animationBuilder.delay

        FXGL.getApp().addFXGLListener(listener)

        bindProperties()

        animation.onFinished = EventHandler {

            state = AnimationState.FINISHED

            FXGL.getApp().removeFXGLListener(listener)

            unbindProperties()
        }
    }

    abstract fun buildAnimation(): Animation

    abstract fun bindProperties()

    abstract fun unbindProperties()

    fun play() {
        if (state == AnimationState.FINISHED) {
            log.warning("Attempted to play finished animation")
            return
        }

        animation.play()
        state = AnimationState.PLAYING
    }

    fun pause() {
        if (state == AnimationState.FINISHED || state == AnimationState.INIT) {
            log.warning("Attempted to pause finished or initializing animation")
            return
        }

        animation.pause()
        state = AnimationState.PAUSED
    }

    fun finish() {
        if (state == AnimationState.FINISHED) {
            log.warning("Attempted to finish already finished animation")
            return
        }

        animation.stop()
        state = AnimationState.FINISHED
    }
}