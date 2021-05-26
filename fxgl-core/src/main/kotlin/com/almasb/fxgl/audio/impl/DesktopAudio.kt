/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio.impl

import com.almasb.fxgl.audio.Audio
import com.almasb.fxgl.audio.AudioType
import javafx.scene.media.AudioClip
import javafx.scene.media.MediaPlayer

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DesktopMusic(private val mediaPlayer: MediaPlayer) : Audio(AudioType.MUSIC) {

    override fun setLooping(looping: Boolean) {
        mediaPlayer.cycleCount = if (looping) Integer.MAX_VALUE else 1
    }

    override fun setVolume(volume: Double) {
        mediaPlayer.volume = volume
    }

    override fun setOnFinished(action: Runnable) {
        mediaPlayer.onEndOfMedia = action
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

class DesktopSound(private val clip: AudioClip) : Audio(AudioType.SOUND) {

    override fun setLooping(looping: Boolean) {
        clip.cycleCount = if (looping) Integer.MAX_VALUE else 1
    }

    override fun setVolume(volume: Double) {
        clip.volume = volume
    }

    override fun setOnFinished(action: Runnable) {
        // no-op
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
        // no-op
    }
}