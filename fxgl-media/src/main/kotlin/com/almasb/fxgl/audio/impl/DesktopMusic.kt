/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio.impl

import com.almasb.fxgl.audio.Audio
import com.almasb.fxgl.audio.AudioType
import javafx.scene.media.MediaPlayer

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DesktopMusic(fullName: String, private val mediaPlayer: MediaPlayer) : Audio(AudioType.MUSIC, fullName) {

    override fun setLooping(looping: Boolean) {
        mediaPlayer.setCycleCount(if (looping) Integer.MAX_VALUE else 1)
    }

    override fun setVolume(volume: Double) {
        mediaPlayer.setVolume(volume)
    }

    override fun setOnFinished(action: Runnable) {
        mediaPlayer.setOnEndOfMedia(action)
    }

    override fun play() {
        mediaPlayer.play()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun dispose() {
        mediaPlayer.dispose()
    }
}