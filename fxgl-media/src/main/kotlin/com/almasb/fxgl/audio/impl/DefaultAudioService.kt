/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio.impl

import com.almasb.fxgl.audio.Audio
import com.almasb.fxgl.audio.AudioService
import com.almasb.fxgl.audio.AudioType
import java.net.URL
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class DefaultAudioService : AudioService {

    private val cache = HashMap<String, Audio>()

    override fun loadAudio(type: AudioType, resourceURL: URL): Audio {
        val url = resourceURL.toExternalForm()

        var audio: Audio? = cache[url]

        if (audio == null) {
            audio = loadAudioImpl(type, resourceURL)

            cache[url] = audio
        }

        return audio
    }

    override fun unloadAudio(audio: Audio) {
        cache.remove(audio.resourceName)
        audio.dispose()
    }

    protected abstract fun loadAudioImpl(type: AudioType, resourceURL: URL): Audio
}