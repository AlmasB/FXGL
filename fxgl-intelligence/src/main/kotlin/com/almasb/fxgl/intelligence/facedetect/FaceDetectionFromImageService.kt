/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.facedetect

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.intelligence.WebAPI
import com.almasb.fxgl.intelligence.WebAPIService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.ws.LocalWebSocketServer
import com.almasb.fxgl.texture.toBase64
import javafx.scene.image.Image
import java.util.function.Consumer

/**
 * Service that provides access to face detection from a given image.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class FaceDetectionFromImageService : WebAPIService(
    LocalWebSocketServer("FaceDetectionServer", WebAPI.FACE_DETECTION_PORT),
    WebAPI.FACE_DETECTION_API
) {

    private val log = Logger.get(FaceDetectionFromImageService::class.java)

    private val faceDataHandlers = arrayListOf<Consumer<Face>>()

    private fun initService() {
        log.debug("initService()")

        setReady()
    }

    private fun onFaceInput(message: String) {
        try {
            val rawData = message.split(",").filter { it.isNotEmpty() }

            val id = rawData[0].toInt()
            val x = rawData[1].toInt()
            val y = rawData[2].toInt()
            val w = rawData[3].toInt()
            val h = rawData[4].toInt()
            val score = rawData[5].toDouble()

            Async.startAsyncFX {
                faceDataHandlers.forEach { it.accept(Face(id, x, y, w, h)) }
            }

        } catch (e: Exception) {
            log.warning("Failed to parse message.", e)
        }
    }

    /**
     * Add input handler for face recognition data.
     * Input handlers are called on the JavaFX thread.
     */
    fun addInputHandler(handler: Consumer<Face>) {
        faceDataHandlers += handler
    }

    fun removeInputHandler(handler: Consumer<Face>) {
        faceDataHandlers -= handler
    }

    fun detect(image: Image) {
        Async.startAsync {
            rpcRun("detect", "data:image/png;base64," + image.toBase64())
        }
    }
}