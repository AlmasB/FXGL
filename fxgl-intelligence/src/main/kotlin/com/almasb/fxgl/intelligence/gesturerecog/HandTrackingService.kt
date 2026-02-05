/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.gesturerecog

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.intelligence.WebAPI
import com.almasb.fxgl.intelligence.WebAPIService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.ws.LocalWebSocketServer
import com.almasb.fxgl.net.ws.VideoInputDeviceInfo
import javafx.geometry.Point3D
import java.util.function.Consumer

/**
 * Service that provides access to hand tracking.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class HandTrackingService : WebAPIService(
    LocalWebSocketServer("HandTrackingServer", WebAPI.GESTURE_RECOGNITION_PORT),
    WebAPI.GESTURE_RECOGNITION_API
) {

    private val log = Logger.get(HandTrackingService::class.java)

    private val videoInputDevices = arrayListOf<VideoInputDeviceInfo>()

    private val handDataHandlers = arrayListOf<Consumer<Hand>>()

    var onMediaDeviceDetectionCompleted: Runnable = EmptyRunnable

    val videoDevices: List<VideoInputDeviceInfo>
        get() = videoInputDevices.toList()

    val landmarksView: HandLandmarksView by lazy {
        HandLandmarksView().also { addInputHandler(it) }
    }

    // kind: audioinput, videoinput, audiooutput
    private fun onMediaDeviceDetected(kind: String, label: String, deviceID: String) {
        log.debug("New media device detected: $kind,$label,$deviceID")

        if (kind == "videoinput") {
            videoInputDevices += VideoInputDeviceInfo(label, deviceID)
        }
    }

    private fun onMediaDeviceDetectionComplete() {
        Async.startAsyncFX {
            onMediaDeviceDetectionCompleted.run()
        }
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

    fun setVideoDevice(videoDevice: VideoInputDeviceInfo) {
        log.debug("setting video device = $videoDevice")

        rpcRun("setVideoInputDevice", videoDevice.id)
    }
}