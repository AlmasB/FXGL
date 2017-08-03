/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.listener.StateListener
import com.almasb.fxgl.util.EmptyRunnable

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ParallelAnimation(var cycleCount: Int = 1, vararg animations: Animation<*>) : StateListener {

    constructor(vararg animations: Animation<*>) : this(1, *animations)

    private val animations: List<Animation<*>> = animations.toList()

    var isAutoReverse = false
    var onFinished: Runnable = EmptyRunnable

    private var count = 0

    var isReverse = false
        private set

    var isPaused = false
        private set

    /**
     * True between start and stop.
     * Pauses have no effect on this flag.
     */
    var isAnimating = false
        private set

    private var checkDelay = true

    init {
        if (animations.isEmpty())
            throw IllegalArgumentException("Animation list is empty!")
    }

    /**
     * State in which we are animating.
     */
    private lateinit var state: State

    fun startInPlayState() {
        start(FXGL.getApp().stateMachine.playState)
    }

    fun startReverse(state: State) {
        if (!isAnimating) {
            isReverse = true
            start(state)
        }
    }

    fun start(state: State) {
        if (!isAnimating) {
            this.state = state
            isAnimating = true
            state.addStateListener(this)

            // reset animations, then start
            if (isReverse) {
                animations.forEach {
                    (it as Animation<Any>).onProgress(it.animatedValue.getValue(1.0))
                    it.startReverse(state)
                }
            } else {
                animations.forEach {
                    (it as Animation<Any>).onProgress(it.animatedValue.getValue(0.0))
                    it.start(state)
                }
            }
        }
    }

    fun stop() {
        if (isAnimating) {
            isAnimating = false
            state.removeStateListener(this)
            count = 0
            isReverse = false
            checkDelay = true
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    override fun onUpdate(tpf: Double) {
        if (isPaused)
            return

        val isFinished = animations.none { it.isAnimating }

        if (isFinished) {
            count++

            if (count >= cycleCount) {
                onFinished.run()
                stop()
                return
            } else {
                if (isAutoReverse) {
                    isReverse = !isReverse

                    // reset animations, then start
                    if (isReverse) {
                        animations.forEach {
                            (it as Animation<Any>).onProgress(it.animatedValue.getValue(1.0))
                            it.startReverse(state)
                        }
                    } else {
                        animations.forEach {
                            (it as Animation<Any>).onProgress(it.animatedValue.getValue(0.0))
                            it.start(state)
                        }
                    }
                }
            }
        }
    }
}