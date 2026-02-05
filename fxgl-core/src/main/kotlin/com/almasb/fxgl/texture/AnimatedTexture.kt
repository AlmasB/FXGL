/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.animation.AnimatedValue
import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationBuilder
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.util.EmptyRunnable
import javafx.animation.Interpolator
import javafx.util.Duration
import kotlin.math.min

/**
 * Represents an animated texture.
 * Animation channels, like WALK, RUN, IDLE, ATTACK, etc. can be set dynamically to alter the animation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimatedTexture(defaultChannel: AnimationChannel) : Texture(defaultChannel.image) {

    private var currentFrame = 0

    private lateinit var animation: Animation<Int>

    var animationChannel: AnimationChannel = defaultChannel
        private set(value) {
            field = value
            updateAnimation()
        }

    var onCycleFinished: Runnable = EmptyRunnable

    var interpolator: Interpolator = Interpolators.LINEAR.EASE_OUT()
        set(value) {
            field = value
            animation.interpolator = interpolator
        }

    init {
        // force channel to apply settings to this texture
        updateImage()

        updateAnimation()
    }

    /**
     * Plays given animation [channel] from start to end once.
     * The animation will stop on the last frame.
     */
    fun playAnimationChannel(channel: AnimationChannel) {
        animationChannel = channel

        animation.stop()
        animation.cycleCount = 1
        animation.start()
    }

    /**
     * Plays given animation [channel] from specific [startFrame] to end once.
     * The animation will stop on the last frame.
     */
    fun playAnimationChannel(channel: AnimationChannel, startFrame: Int) {
        playAnimationChannel(channel)

        currentFrame = startFrame
        animation.setTimeTo(currentFrame.toDouble() * animationChannel.frameDuration)
    }

    /**
     * Loops given [channel].
     *
     * Note: if the given channel is already playing or looping, then noop.
     */
    fun loopNoOverride(channel: AnimationChannel) {
        if (animationChannel === channel && animation.isAnimating)
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

        animation.stop()
        animation.cycleCount = Int.MAX_VALUE
        animation.start()
    }

    /**
     * Play the last animation channel (or default) from end to start once.
     * The animation will stop at the first frame.
     */
    fun playReverse(): AnimatedTexture {
        animation.stop()
        animation.cycleCount = 1
        animation.startReverse()

        return this
    }

    /**
     * Loops the last animation channel (or default) from end to start.
     */
    fun loopReverse(): AnimatedTexture {
        animation.stop()
        animation.cycleCount = Int.MAX_VALUE
        animation.startReverse()

        return this
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
     * Play the last animation channel (or default) from specific [startFrame] to end once.
     * The animation will stop on the last frame.
     */
    fun playFrom(startFrame: Int): AnimatedTexture {
        playAnimationChannel(animationChannel, startFrame)
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
        animation.stop()

        currentFrame = 0

        updateImage()
    }

    // play and loop
    // play would stop at last frame
    // loop would set the 0th frame

    override fun onUpdate(tpf: Double) {
        animation.onUpdate(tpf)
    }

    private fun updateImage() {
        val frameData = animationChannel.getFrameData(currentFrame)

        image = animationChannel.image
        fitWidth = frameData.width.toDouble()
        fitHeight = frameData.height.toDouble()
        viewport = frameData.viewport

        layoutX = frameData.offsetX.toDouble()
        layoutY = frameData.offsetY.toDouble()
    }

    private fun updateAnimation() {
        animation = AnimationBuilder()
                .onCycleFinished {
                    if (animation.cycleCount > 1) {
                        currentFrame = 0
                        updateImage()
                    }

                    onCycleFinished.run()
                }
                .duration(Duration.seconds(animationChannel.frameDuration * animationChannel.sequence.size))
                .interpolator(interpolator)
                .animate(PreciseAnimatedIntValue(0, animationChannel.sequence.size - 1))
                .onProgress { frameNum ->
                    currentFrame = min(frameNum, animationChannel.sequence.size - 1)
                    updateImage()
                }
                .build()
    }
}

/**
 * This animated value provides higher accuracy for mapping progress to an int value.
 *
 * For example, the default AnimatedValue results for [0,3]  (non-uniform!):
0.00: 0
0.05: 0
0.10: 0
0.15: 0

0.20: 1
0.25: 1
0.30: 1
0.35: 1
0.40: 1
0.45: 1

0.50: 2
0.55: 2
0.60: 2
0.65: 2
0.70: 2
0.75: 2
0.80: 2

0.85: 3
0.90: 3
0.95: 3
1.00: 3
 */
private class PreciseAnimatedIntValue(start: Int, end: Int) : AnimatedValue<Int>(start, end) {

    private val mapping = linkedMapOf<ClosedFloatingPointRange<Double>, Int>()

    init {
        val numSegments = end - start + 1
        val progressPerSegment = 1.0 / numSegments

        var progress = 0.0

        for (i in start..end) {
            val progressEnd = if (i == end) 1.0 else (progress + progressPerSegment)

            mapping[progress..progressEnd] = i

            progress += progressPerSegment
        }
    }

    override fun animate(val1: Int, val2: Int, progress: Double, interpolator: Interpolator): Int {
        val p = interpolator.interpolate(0.0, 1.0, progress)

        return mapping.filterKeys { p in it }
                .values
                .lastOrNull() ?: val1
    }
}