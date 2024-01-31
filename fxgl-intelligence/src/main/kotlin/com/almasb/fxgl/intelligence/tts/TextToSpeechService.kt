/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.tts

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.intelligence.WebAPI
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.ws.LocalWebSocketServer
import com.almasb.fxgl.speechrecog.SpeechRecognitionService
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

/**
 * TODO: remove duplicate code
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class TextToSpeechService : EngineService() {

    private val log = Logger.get(TextToSpeechService::class.java)
    private val server = LocalWebSocketServer("TTSServer", WebAPI.TEXT_TO_SPEECH_PORT)

    private var webDriver: WebDriver? = null

    override fun onInit() {
        server.start()
    }

    /**
     * Starts this service in a background thread.
     * Can be called after stop() to restart the service.
     * If the service has already started, then calls stop() and restarts it.
     */
    fun start() {
        Async.startAsync {
            try {
                if (webDriver != null) {
                    stop()
                }

                val options = ChromeOptions()
                options.addArguments("--headless=new")
                options.addArguments("--use-fake-ui-for-media-stream")

                webDriver = ChromeDriver(options)
                webDriver!!.get(WebAPI.TEXT_TO_SPEECH_API)

                // TODO: update web-api impl
                // force it to play, so the audio output is initialized
                webDriver!!.findElement(By.id("play")).click()

                // we are ready to use the web api service
            } catch (e: Exception) {
                log.warning("Failed to start Chrome web driver. Ensure Chrome is installed in default location")
                log.warning("Error data", e)
            }
        }
    }

    /**
     * Stops this service.
     * No-op if it has not started via start() before.
     */
    fun stop() {
        try {
            if (webDriver != null) {
                webDriver!!.quit()
                webDriver = null
            }
        } catch (e: Exception) {
            log.warning("Failed to quit web driver", e)
        }
    }

    fun speak(text: String) {
        server.send(text)
    }

    override fun onExit() {
        stop()
        server.stop()
    }
}