/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio.impl

import com.almasb.fxgl.audio.Audio
import com.almasb.fxgl.audio.AudioType
import javafx.scene.media.AudioClip

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DesktopSound(fullName: String, private val clip: AudioClip) : Audio(AudioType.SOUND, fullName) {

    override fun setLooping(looping: Boolean) {
        clip.setCycleCount(if (looping) Integer.MAX_VALUE else 1)
    }

    override fun setVolume(volume: Double) {
        clip.setVolume(volume)
    }

    override fun setOnFinished(action: Runnable) {

    }

    override fun play() {
        clip.play()
    }

    override fun pause() {
        clip.stop()
    }

    override fun stop() {
        clip.stop()
    }

    override fun dispose() {

    }
}