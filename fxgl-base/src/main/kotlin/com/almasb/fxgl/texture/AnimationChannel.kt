/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.app.FXGL
import javafx.scene.image.Image
import javafx.util.Duration

/**
 * Represents a single animation channel (cycle) from a sprite sheet.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimationChannel(val image: Image,
                       val framesPerRow: Int,
                       val frameWidth: Int,
                       val frameHeight: Int,
                       channelDuration: Duration,
                       startFrame: Int,
                       endFrame: Int) {

    constructor(assetName: String,
                framesPerRow: Int,
                frameWidth: Int,
                frameHeight: Int,
                channelDuration: Duration,
                startFrame: Int,
                endFrame: Int) : this(FXGL.getAssetLoader().loadTexture(assetName).image,
                    framesPerRow, frameWidth, frameHeight, channelDuration, startFrame, endFrame)

    internal val sequence = arrayListOf<Int>()

    // seconds
    internal val frameDuration: Double

    init {
        sequence += startFrame..endFrame
        frameDuration = channelDuration.toSeconds() / sequence.size
    }

    fun isLastFrame(frame: Int) = frame == sequence.size - 1

    /**
     * Returns next frame index or 0 if [frame] is last.
     */
    fun frameAfter(frame: Int) = (frame + 1) % sequence.size
}