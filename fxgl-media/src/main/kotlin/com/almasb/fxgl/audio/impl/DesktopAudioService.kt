/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio.impl

import com.almasb.fxgl.audio.Audio
import com.almasb.fxgl.audio.AudioType
import javafx.scene.media.AudioClip
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DesktopAudioService : DefaultAudioService() {

    override fun loadAudioImpl(type: AudioType, resourceName: String): Audio {
        val url = javaClass.getResource(resourceName).toExternalForm()

        return if (type === AudioType.MUSIC) {
            DesktopMusic(resourceName, MediaPlayer(Media(url)))
        } else {
            DesktopSound(resourceName, AudioClip(url))
        }
    }
}