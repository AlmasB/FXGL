/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.speechrecog

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.intelligence.WebAPI
import com.almasb.fxgl.intelligence.WebAPIService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.ws.LocalWebSocketServer
import java.util.function.BiConsumer

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
class SpeechRecognitionService : WebAPIService(
        LocalWebSocketServer("SpeechRecogServer", WebAPI.SPEECH_RECOGNITION_PORT),
        WebAPI.SPEECH_RECOGNITION_API
) {

    private val log = Logger.get(SpeechRecognitionService::class.java)

    private val speechInputHandlers = arrayListOf<BiConsumer<String, Double>>()

    /**
     * Add a [handler] for incoming speech input.
     */
    fun addInputHandler(handler: BiConsumer<String, Double>) {
        speechInputHandlers += handler
    }

    /**
     * Remove an existing input handler.
     */
    fun removeInputHandler(handler: BiConsumer<String, Double>) {
        speechInputHandlers -= handler
    }

    private fun initService() {
        setReady()
    }

    private fun onSpeechInput(text: String, confidence: Double) {
        log.debug("Received speech input ($confidence): $text")

        Async.startAsyncFX {
            speechInputHandlers.forEach { it.accept(text, confidence) }
        }
    }
}