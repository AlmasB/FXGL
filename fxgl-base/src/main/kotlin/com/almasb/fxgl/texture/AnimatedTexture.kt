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

    private var playingChannel = false

    var animationChannel: AnimationChannel? = null
        set(value) {
            if (field !== value && !playingChannel) {
                reset()
                field = value
            }
        }

    init {
        animationChannel = defaultChannel

        // force channel to apply settings to this texture
        onUpdate(0.0)

        start(FXGL.getApp().stateMachine.playState)
    }

    /**
     * Plays given animation channel from start to end.
     * The channel cannot be reset while playing, but
     * you can call this method instead to reset the channel.
     */
    fun playAnimationChannel(channel: AnimationChannel) {
        animationChannel = channel
        playingChannel = true
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

    override fun onUpdate(tpf: Double) {
        animationChannel?.let {

            if (counter >= it.frameDuration) {

                // frame done

                if (currentFrame == it.sequence.size-1) {
                    // channel done
                    if (playingChannel) {
                        playingChannel = false
                    }
                }

                currentFrame = (currentFrame + 1) % it.sequence.size
                counter = 0.0

                // TODO: this is a quick hack, ideally we should have two modes:
                // play and loop
                // play would stop at last frame
                // loop would set the 0th frame

                return
            }

            counter += tpf

            val framesPerRow = it.framesPerRow

            val frameWidth = it.frameWidth.toDouble()
            val frameHeight = it.frameHeight.toDouble()

            val row = it.sequence[currentFrame] / framesPerRow
            val col = it.sequence[currentFrame] % framesPerRow

            image = it.image
            fitWidth = frameWidth
            fitHeight = frameHeight
            viewport = Rectangle2D(col * frameWidth, row * frameHeight,
                    frameWidth, frameHeight)
        }
    }

    private fun reset() {
        currentFrame = 0
        counter = 0.0
    }

    override fun dispose() {
        stop()
        super.dispose()
    }
}