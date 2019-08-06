/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio

import com.almasb.fxgl.core.Disposable

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

private val audio: Audio by lazy {
    object : Audio(AudioType.SOUND, "") {
        override fun setLooping(looping: Boolean) {}
        override fun setVolume(volume: Double) {}
        override fun setOnFinished(action: Runnable) {}
        override fun play() {}
        override fun pause() {}
        override fun stop() {}
        override fun dispose() {}
    }
}

fun getDummyAudio() = audio

/**
 * Represents a long-term audio in mp3 file.
 * Use for background (looping) music or recorded dialogues.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Music(internal val audio: Audio) : Disposable {

    internal var isDisposed = false

    override fun dispose() {
        isDisposed = true
    }
}

/**
 * Represents a short sound in .wav file.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Sound(internal val audio: Audio): Disposable {

    internal var isDisposed = false

    override fun dispose() {
        isDisposed = true
    }
}