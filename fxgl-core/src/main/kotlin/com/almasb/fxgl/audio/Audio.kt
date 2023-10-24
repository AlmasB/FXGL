/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio

import com.almasb.fxgl.core.Disposable
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty

enum class AudioType(val volume: DoubleProperty) {
    MUSIC(SimpleDoubleProperty(0.5)),
    SOUND(SimpleDoubleProperty(0.5))
}

/**
 * An abstraction around a native audio format.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Audio(val type: AudioType) {

    internal fun mix(volume: Double) : Double {
        return type.volume.value * volume
    }

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
    internal abstract fun dispose()
}

private val audio: Audio by lazy {
    object : Audio(AudioType.SOUND) {
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

abstract class Media(internal val audio : Audio) : Disposable {

    internal var isDisposed = false
    internal val volume = SimpleDoubleProperty(1.0)

    init {
        audio.type.volume.addListener { _, _, _ ->
            audio.setVolume(volume.value)
        }
        volume.addListener { _, _, _ ->
            audio.setVolume(volume.value)
        }
    }

    fun setVolume(value: Double) {
        volume.value = value
    }

    fun getVolume() : Double {
        return volume.value
    }

    fun volumeProperty() : DoubleProperty {
        return volume
    }

    fun setLooping(looping: Boolean) {
        audio.setLooping(looping)
    }

    override fun dispose() {
        isDisposed = true
    }
}

/**
 * Represents a long-term audio in mp3 file.
 * Use for background (looping) music or recorded dialogues.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Music(audio : Audio) : Media(audio) {

}

/**
 * Represents a short sound in .wav file.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Sound(audio : Audio) : Media(audio) {

}