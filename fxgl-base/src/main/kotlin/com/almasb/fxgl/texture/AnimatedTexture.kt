/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.listener.StateListener
import javafx.geometry.Rectangle2D

/**
 * Represents an animated texture.
 * Animation channels, like WALK, RUN, IDLE, ATTACK, etc. can
 * be set dynamically to alter the animation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimatedTexture(defaultChannel: AnimationChannel) : Texture(defaultChannel.image), StateListener {

    private var currentFrame = 0
    private var counter = 0.0

    private var looping = false
    private var needUpdate = false

    var animationChannel: AnimationChannel? = null
        private set

    var onCycleFinished: Runnable? = null

    init {
        animationChannel = defaultChannel

        // force channel to apply settings to this texture
        updateImage()

        start(FXGL.getApp().stateMachine.playState)
    }

    /**
     * Plays given animation channel from start to end.
     * The channel cannot be reset while playing, but
     * you can call this method instead to reset the channel.
     */
    fun playAnimationChannel(channel: AnimationChannel) {
        animationChannel = channel
        looping = false
        reset()
    }

    /**
     * Loops given channel.
     * Calling this again on the same channel will have no effect
     * and the channel will not be reset,
     * but calling with a different channel will switch to that channel.
     */
    fun loopAnimationChannel(channel: AnimationChannel) {
        animationChannel = channel
        looping = true
        reset()
    }

    fun play() {
        playAnimationChannel(animationChannel!!)
    }

    fun loop() {
        loopAnimationChannel(animationChannel!!)
    }

    private lateinit var state: State

    var started = false
        private set

    fun start(state: State) {
        if (started) {
            return
        }

        this.state = state
        state.addStateListener(this)
        started = true
    }

    fun stop() {
        if (!started) {
            return
        }

        state.removeStateListener(this)
        reset()
        started = false
    }

    // play and loop
    // play would stop at last frame
    // loop would set the 0th frame

    override fun onUpdate(tpf: Double) {
        if (!needUpdate)
            return

        animationChannel?.let {

            var channelDone = false

            counter += tpf

            if (counter >= it.frameDuration) {

                // frame done
                if (it.isLastFrame(currentFrame)) {

                    channelDone = true

                    if (!looping) {
                        // stop at last frame, do not update image
                        needUpdate = false
                        onCycleFinished()
                        return
                    }
                }

                counter = 0.0
                currentFrame = it.frameAfter(currentFrame)

                updateImage()
            }

            if (channelDone) {
                onCycleFinished()
            }
        }
    }

    private fun onCycleFinished() {
        onCycleFinished?.run()
    }

    private fun updateImage() {
        animationChannel?.let {
            val framesPerRow = it.framesPerRow

            val frameWidth = it.frameWidth.toDouble()
            val frameHeight = it.frameHeight.toDouble()

            val row = it.sequence[currentFrame] / framesPerRow
            val col = it.sequence[currentFrame] % framesPerRow

            image = it.image
            fitWidth = frameWidth
            fitHeight = frameHeight
            viewport = Rectangle2D(col * frameWidth, row * frameHeight, frameWidth, frameHeight)
        }
    }

    private fun reset() {
        currentFrame = 0
        counter = 0.0
        needUpdate = true

        updateImage()
    }

    override fun dispose() {
        stop()
        super.dispose()
    }
}