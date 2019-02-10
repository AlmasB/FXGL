/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio

enum class AudioType {
    MUSIC, SOUND
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Audio(val type: AudioType, val resourceName: String) {

    abstract fun setLooping(looping: Boolean)

    abstract fun setVolume(volume: Double)

    abstract fun setOnFinished(action: Runnable)

    abstract fun play()

    abstract fun pause()

    abstract fun stop()

    /**
     * Do NOT call directly.
     * This is called automatically by the service managing this audio.
     */
    abstract fun dispose()
}