/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.core.util.EmptyRunnable
import javafx.geometry.Rectangle2D

/**
 * Represents an animated texture.
 * Animation channels, like WALK, RUN, IDLE, ATTACK, etc. can be set dynamically to alter the animation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimatedTexture(defaultChannel: AnimationChannel) : Texture(defaultChannel.image) {

    private var currentFrame = 0
    private var counter = 0.0

    private var isLooping = false
    private var needUpdate = false

    var animationChannel: AnimationChannel = defaultChannel
        private set

    var onCycleFinished: Runnable = EmptyRunnable

    init {
        // force channel to apply settings to this texture
        updateImage()
    }

    /**
     * Plays given animation [channel] from start to end once.
     * The animation will stop at the last frame.
     */
    fun playAnimationChannel(channel: AnimationChannel) {
        animationChannel = channel
        isLooping = false
        reset()
    }

    /**
     * Loops given [channel].
     *
     * Note: if the given channel is already playing or looping, then noop.
     */
    fun loopNoOverride(channel: AnimationChannel) {
        if (animationChannel === channel)
            return

        loopAnimationChannel(channel)
    }

    /**
     * Loops given [channel].
     *
     * Note: any channel that is already playing or looping will be overridden.
     */
    fun loopAnimationChannel(channel: AnimationChannel) {
        animationChannel = channel
        isLooping = true
        reset()
    }

    /**
     * Play the last animation channel (or default) from start to end once.
     * The animation will stop at the last frame.
     */
    fun play(): AnimatedTexture {
        playAnimationChannel(animationChannel)
        return this
    }

    /**
     * Loops the last animation channel (or default).
     */
    fun loop(): AnimatedTexture {
        loopAnimationChannel(animationChannel)
        return this
    }

    /**
     * Stop the animation.
     * The frame will be set to 0th (i.e. the first frame).
     */
    fun stop() {
        currentFrame = 0
        counter = 0.0
        needUpdate = false

        updateImage()
    }

    // play and loop
    // play would stop at last frame
    // loop would set the 0th frame

    override fun onUpdate(tpf: Double) {
        if (!needUpdate)
            return

        var channelDone = false

        counter += tpf

        if (counter >= animationChannel.frameDuration) {

            // frame done
            if (animationChannel.isLastFrame(currentFrame)) {

                channelDone = true

                if (!isLooping) {
                    // stop at last frame, do not update image
                    needUpdate = false
                    onCycleFinished()
                    return
                }
            }

            counter = 0.0
            currentFrame = animationChannel.frameAfter(currentFrame)

            updateImage()
        }

        if (channelDone) {
            onCycleFinished()
        }
    }

    private fun reset() {
        currentFrame = 0
        counter = 0.0
        needUpdate = true

        updateImage()
    }

    private fun updateImage() {
        val frameData = animationChannel.getFrameData(currentFrame)

        image = animationChannel.image
        fitWidth = frameData.width.toDouble()
        fitHeight = frameData.height.toDouble()
        viewport = Rectangle2D(frameData.x.toDouble(), frameData.y.toDouble(), frameData.width.toDouble(), frameData.height.toDouble())
    }

    private fun onCycleFinished() {
        onCycleFinished.run()
    }
}