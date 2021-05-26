/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.audio.impl

import com.almasb.fxgl.audio.AudioType
import com.almasb.fxgl.audio.getDummyAudio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class AudioLoaderTest {

    private lateinit var loader: AudioLoader

    @BeforeEach
    fun setUp() {
        loader = DesktopAndMobileAudioLoader()
    }

    @Test
    fun `Load sound file`() {
        val audio = loader.loadAudio(AudioType.SOUND, javaClass.getResource("sound_effect.wav"), isDesktop = true)

        assertThat(audio, `is`(not(getDummyAudio())))
    }

    @Test
    fun `Loading on mobile does not crash if Attach is not present`() {
        val audio = loader.loadAudio(AudioType.SOUND, javaClass.getResource("sound_effect.wav"), isDesktop = false)

        assertThat(audio, `is`(getDummyAudio()))
    }
}