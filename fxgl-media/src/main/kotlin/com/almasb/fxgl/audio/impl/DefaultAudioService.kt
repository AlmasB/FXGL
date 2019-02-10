/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio.impl

import com.almasb.fxgl.audio.Audio
import com.almasb.fxgl.audio.AudioService
import com.almasb.fxgl.audio.AudioType
import java.util.HashMap

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class DefaultAudioService : AudioService {

    private val cache = HashMap<String, Audio>()

    override fun loadAudio(type: AudioType, resourceName: String): Audio {
        var audio: Audio? = cache[resourceName]

        if (audio == null) {
            try {
                audio = loadAudioImpl(type, resourceName)
            } catch (e: Exception) {
                throw RuntimeException("Failed to load audio", e)
            }

            cache[resourceName] = audio
        }

        return audio
    }

    override fun unloadAudio(audio: Audio) {
        cache.remove(audio.resourceName)
        audio.dispose()
    }

    protected abstract fun loadAudioImpl(type: AudioType, resourceName: String): Audio
}