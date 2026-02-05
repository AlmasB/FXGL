/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.gesturerecog

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.math.FXGLMath
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.util.Duration
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.min

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
class HandLandmarksView : Pane(), Consumer<Hand> {

    /**
     * Allows the user configure how the hands are to be visualised.
     */
    val config = HandViewConfig(
        scaleX = 600.0,
        scaleY = 400.0,
        scaleZ = 120.0,
        minRadius = 3.0,
        maxRadius = 7.0,
        keepVisibleDuration = Duration.seconds(0.4)
    )

    // our backend supports exactly 2 hands
    private val handView1 = HandView()
    private val handView2 = HandView()

    init {
        children += handView1
        children += handView2
    }

    override fun accept(hand: Hand) {
        Async.startAsyncFX {
            if (hand.id == 0) {
                handView1.update(hand, config)
            }

            if (hand.id == 1) {
                handView2.update(hand, config)
            }

            val now = System.currentTimeMillis()

            // TODO: this isn't quite right because accept() is only called when there is data available
            // so isVisible = false is never needed
            if (now - handView1.lastTimeVisibleMillis > config.keepVisibleDuration.toMillis()) {
                handView1.isVisible = false
            }

            if (now - handView2.lastTimeVisibleMillis >= config.keepVisibleDuration.toMillis()) {
                handView2.isVisible = false
            }
        }
    }
}

data class HandViewConfig(
    var scaleX: Double,
    var scaleY: Double,
    var scaleZ: Double,
    var minRadius: Double,
    var maxRadius: Double,
    var keepVisibleDuration: Duration
)

private class HandView : Pane() {

    var lastTimeVisibleMillis = 0L

    // nodes
    private val landmarks = Array(21) { Circle(5.0, 5.0, 5.0, Color.RED) }
    // edges
    private val connections = Array(21) { Line() }

    // from https://ai.google.dev/edge/mediapipe/solutions/vision/hand_landmarker
    private val connectionPairs = listOf(
        0 to 1,
        1 to 2,
        2 to 3,
        3 to 4,
        0 to 5,
        5 to 6,
        6 to 7,
        7 to 8,
        5 to 9,
        9 to 10,
        10 to 11,
        11 to 12,
        9 to 13,
        13 to 14,
        14 to 15,
        15 to 16,
        13 to 17,
        0 to 17,
        17 to 18,
        18 to 19,
        19 to 20
    )

    init {
        connections.forEach { it.stroke = Color.GREEN }

        children.addAll(connections)
        children.addAll(landmarks)

        isVisible = false
    }

    fun update(hand: Hand, config: HandViewConfig) {
        hand.points.forEachIndexed { i, p ->
            landmarks[i].translateX = (1.0 - p.x) * config.scaleX
            landmarks[i].translateY = p.y * config.scaleY

            var radius = FXGLMath.abs(p.z) * config.scaleZ
            radius = max(radius, config.minRadius)
            radius = min(radius, config.maxRadius)

            landmarks[i].centerX = radius
            landmarks[i].centerY = radius
            landmarks[i].radius = radius
        }

        connectionPairs.forEachIndexed { i, pair ->
            val p1 = landmarks[pair.first]
            val p2 = landmarks[pair.second]

            val r1 = landmarks[pair.first].radius
            val r2 = landmarks[pair.second].radius

            connections[i].startX = p1.translateX + r1
            connections[i].startY = p1.translateY + r1
            connections[i].endX = p2.translateX + r2
            connections[i].endY = p2.translateY + r2
        }

        isVisible = true
        lastTimeVisibleMillis = System.currentTimeMillis()
    }
}