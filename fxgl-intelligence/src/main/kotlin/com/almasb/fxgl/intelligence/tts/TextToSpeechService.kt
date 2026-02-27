/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.tts

import com.almasb.fxgl.intelligence.WebAPI
import com.almasb.fxgl.intelligence.WebAPIService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.ws.LocalWebSocketServer
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
class TextToSpeechService : WebAPIService(
        LocalWebSocketServer("TTSServer", WebAPI.TEXT_TO_SPEECH_PORT),
        WebAPI.TEXT_TO_SPEECH_API
) {

    private val log = Logger.get(TextToSpeechService::class.java)

    private val synthVoices = arrayListOf<Voice>()
    var selectedVoice: Voice = Voice("NULL")

    val voices: List<Voice>
        get() = synthVoices.toList()

    override fun onWebDriverLoaded(webDriver: WebDriver) {
        // force it to play, so the audio output is initialized
        webDriver.findElement(By.id("play")).click()
    }

    private fun initVoices(voiceNames: List<String>) {
        synthVoices += voiceNames.map { Voice(it) }

        if (synthVoices.isNotEmpty()) {
            selectedVoice = synthVoices[0]
        }

        setReady()
    }

    fun speak(text: String) {
        if (!isReady || synthVoices.isEmpty())
            return

        rpcRun("speak", selectedVoice.name, text)
    }
}

data class Voice internal constructor(val name: String)