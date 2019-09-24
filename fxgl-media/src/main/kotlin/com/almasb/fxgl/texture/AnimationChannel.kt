/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import javafx.scene.image.Image
import javafx.util.Duration

/**
 * Represents a single animation channel (cycle) from a sprite sheet.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimationChannel(val image: Image,
                       channelDuration: Duration,

                       /**
                        * Maps frame number in sprite sheet, so may not start with 0, to its data.
                        */
                       private val frameData: List<Pair<Int, FrameData>>) {

    constructor(image: Image,
                channelDuration: Duration,
                numFrames: Int
    ) : this(
            image,
            numFrames,
            image.width.toInt() / numFrames,
            image.height.toInt(),
            channelDuration,
            0,
            numFrames - 1
    )

    constructor(image: Image,
                framesPerRow: Int,
                frameWidth: Int,
                frameHeight: Int,
                channelDuration: Duration,
                startFrame: Int,
                endFrame: Int
    ) : this(

            /*
             * We compute x,y frame data based on given info
             */

            // val col = it.sequence[currentFrame] % framesPerRow
            // val row = it.sequence[currentFrame] / framesPerRow
            // col * frameWidth, row * frameHeight
            image, channelDuration, (startFrame..endFrame).map { it to FrameData((it % framesPerRow) * frameWidth, (it / framesPerRow) * frameHeight, frameWidth, frameHeight) }
    )

    constructor(image: Image,
                channelDuration: Duration,
                framesPerRow: Int,
                animationChannelData: List<AnimationChannelData>
    ) : this(

            /*
             * We compute x,y frame data based on given info
             */

            // val col = it.sequence[currentFrame] % framesPerRow
            // val row = it.sequence[currentFrame] / framesPerRow
            // col * frameWidth, row * frameHeight
            image, channelDuration, animationChannelData.flatMap { data ->
        (data.frameStart..data.frameEnd).map { it to FrameData((it % framesPerRow) * data.frameWidth, (it / framesPerRow) * data.frameHeight, data.frameWidth, data.frameHeight) }
    })

    @JvmOverloads constructor(images: List<Image>,
                channelDuration: Duration,
                numFrames: Int = images.size
    ) : this(
            merge(images),
            channelDuration,
            numFrames
    )

    /**
     * Stores the animation frame numbers in sequence.
     * For example, 13, 14, 17, 18, 20, 22.
     */
    internal val sequence: List<Int> = frameData.map { it.first }

    // seconds
    internal val frameDuration: Double

    init {
        frameDuration = channelDuration.toSeconds() / sequence.size
    }

    fun isLastFrame(frame: Int) = frame == sequence.size - 1

    fun getFrameData(frame: Int): FrameData = frameData.find { it.first == sequence[frame] }!!.second

    fun getFrameWidth(frame: Int) = getFrameData(frame).width
    fun getFrameHeight(frame: Int) = getFrameData(frame).height

    /**
     * Returns next frame index or 0 if [frame] is last.
     */
    fun frameAfter(frame: Int) = (frame + 1) % sequence.size
}

data class AnimationChannelData(
        val frameStart: Int,
        val frameEnd: Int,
        val frameWidth: Int,
        val frameHeight: Int
)

/**
 * Defines a single frame within a sprite sheet.
 */
data class FrameData(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
)