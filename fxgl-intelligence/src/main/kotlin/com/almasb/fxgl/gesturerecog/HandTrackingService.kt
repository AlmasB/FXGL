/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gesturerecog

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.intelligence.WebAPI
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.speechrecog.SpeechRecognitionService
import com.almasb.fxgl.ws.LocalWebSocketServer
import javafx.geometry.Point3D
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.util.function.Consumer

/**
 * TODO: remove duplicate code
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class HandTrackingService : EngineService() {

    private val log = Logger.get(SpeechRecognitionService::class.java)
    private val server = LocalWebSocketServer("HandTrackingServer", WebAPI.GESTURE_RECOGNITION_PORT)

    private var webDriver: WebDriver? = null

    private val handDataHandlers = arrayListOf<Consumer<Hand>>()

    override fun onInit() {
        server.addMessageHandler { message ->
            try {
                val rawData = message.split(",").filter { it.isNotEmpty() }

                val id = rawData[0].toInt()
                val points = ArrayList<Point3D>()

                var i = 1
                while (i < rawData.size) {
                    val x = rawData[i + 0].toDouble()
                    val y = rawData[i + 1].toDouble()
                    val z = rawData[i + 2].toDouble()

                    points.add(Point3D(x, y, z))

                    i += 3
                }

                Async.startAsyncFX {
                    handDataHandlers.forEach { it.accept(Hand(id, points)) }
                }

            } catch (e: Exception) {
                log.warning("Failed to parse message.", e)
            }
        }

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
                webDriver!!.get(WebAPI.GESTURE_RECOGNITION_API)

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

    fun addInputHandler(handler: Consumer<Hand>) {
        handDataHandlers += handler
    }

    fun removeInputHandler(handler: Consumer<Hand>) {
        handDataHandlers -= handler
    }

    override fun onExit() {
        stop()
        server.stop()
    }
}