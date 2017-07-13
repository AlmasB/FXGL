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
class SequentialAnimation(val animations: List<Animation<*>>,
                          var cycleCount: Int = 1) : StateListener {

    var isAutoReverse = false
    var onFinished: Runnable = EmptyRunnable

    private var animationIndex = 0

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
//        if (!isAnimating) {
//            isReverse = true
//            start(state)
//        }
    }

    fun start(state: State) {
//        if (!isAnimating) {
            this.state = state
            //isAnimating = true
            state.addStateListener(this)
        animations[0].start(state)
//        }
    }

    fun stop() {
//        if (isAnimating) {
//            isAnimating = false
//            state.removeStateListener(this)
//            time = 0.0
//            count = 0
//            isReverse = false
//            checkDelay = true
//        }
    }

    override fun onUpdate(tpf: Double) {
        val anim = animations[animationIndex]
        //anim.onUpdate(tpf)

        if (!anim.isAnimating) {
            animationIndex++
            if (animationIndex == animations.size) {
                animationIndex = 0
            }

            animations[animationIndex].start(state)
        }
    }
}