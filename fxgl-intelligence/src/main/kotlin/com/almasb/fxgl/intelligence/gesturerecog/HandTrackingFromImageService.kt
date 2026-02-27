/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.gesturerecog

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.intelligence.WebAPI
import com.almasb.fxgl.intelligence.WebAPIService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.ws.LocalWebSocketServer
import com.almasb.fxgl.texture.fromBufferedImage
import com.almasb.fxgl.texture.toBase64
import javafx.geometry.Point3D
import javafx.scene.image.Image
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList

/**
 * Service that provides access to hand tracking from a given image.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class HandTrackingFromImageService : WebAPIService(
    LocalWebSocketServer("HandTrackingServer", WebAPI.HAND_TRACKING_PORT),
    WebAPI.HAND_TRACKING_API
) {

    private val log = Logger.get(HandTrackingFromImageService::class.java)

    private val handDataHandlers = arrayListOf<Consumer<Hand>>()

    val landmarksView: HandLandmarksView by lazy {
        HandLandmarksView().also { addInputHandler(it) }
    }

    private fun initService() {
        log.debug("initService()")

        setReady()
    }

    private fun onHandInput(message: String) {
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

    /**
     * Add input handler for hand tracking data.
     * Input handlers are called on the JavaFX thread.
     */
    fun addInputHandler(handler: Consumer<Hand>) {
        handDataHandlers += handler
    }

    fun removeInputHandler(handler: Consumer<Hand>) {
        handDataHandlers -= handler
    }

    fun detect(image: Image) {
        Async.startAsync {
            rpcRun("detect", "data:image/png;base64," + image.toBase64())
        }
    }
}