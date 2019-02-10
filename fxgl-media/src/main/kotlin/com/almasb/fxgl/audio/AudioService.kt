/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.audio

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface AudioService {

    fun loadAudio(type: AudioType, resourceName: String): Audio

    fun unloadAudio(audio: Audio)
}