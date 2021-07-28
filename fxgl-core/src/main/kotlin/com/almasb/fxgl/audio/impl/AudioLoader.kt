/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio.impl

import com.almasb.fxgl.audio.Audio
import com.almasb.fxgl.audio.AudioType
import java.net.URL

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface AudioLoader {

    /**
     * Perform an IO operation to load an audio object from [resourceURL] as a given [type].
     * No caching is done by this method -- the IO operation will take place on each call.
     *
     * @param isMobile if we are loading on a mobile platform
     */
    fun loadAudio(type: AudioType, resourceURL: URL, isMobile: Boolean): Audio

    /**
     * Release native resources associated with [audio].
     */
    fun unloadAudio(audio: Audio)
}