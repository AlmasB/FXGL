/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.circuitbreaker

import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.minigames.MiniGameView
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color

private const val CHAR_SIZE = 15.0
private const val CHAR_SPEED = 130.0

private const val WIDTH = 800.0
private const val HEIGHT = 500.0

class CircuitBreakerView(miniGame: CircuitBreakerMiniGame = CircuitBreakerMiniGame()) : MiniGameView<CircuitBreakerMiniGame>(miniGame) {

    private val canvas = Canvas(WIDTH, HEIGHT)
    private val g = canvas.graphicsContext2D

    private var oldPosition = miniGame.startPoint

    private val lineData = arrayListOf<Pair<Point2D, Point2D>>()

    init {
        children += canvas
    }

    override fun onUpdate(tpf: Double) {
        val charPosition = miniGame.charPosition

        g.clearRect(0.0, 0.0, WIDTH, HEIGHT)

        g.fill = Color.DARKGREEN
        g.globalAlpha = 0.9
        g.fillRect(0.0, 0.0, WIDTH, HEIGHT)

        g.stroke = Color.LIGHTGREEN
        g.globalAlpha = 1.0
        g.lineWidth = 6.5
        g.strokeRect(0.0, 0.0, WIDTH, HEIGHT)

        g.fill = Color.BLACK
        g.fillOval(charPosition.x, charPosition.y, CHAR_SIZE, CHAR_SIZE)

        lineData += oldPosition.add(CHAR_SIZE / 2, CHAR_SIZE / 2) to charPosition.add(CHAR_SIZE / 2, CHAR_SIZE / 2)

        g.stroke = Color.BLACK
        g.lineWidth = 2.5
        lineData.forEach { (p0, p1) ->
            g.strokeLine(p0.x, p0.y, p1.x, p1.y)
        }

        g.lineWidth = 3.5
        g.stroke = Color.YELLOWGREEN

        val ratioX = WIDTH / miniGame.maze.width
        val ratioY = HEIGHT / miniGame.maze.height

        for (y in 0 until miniGame.maze.height) {
            for (x in 0 until miniGame.maze.width) {
                val cell = miniGame.maze.getMazeCell(x, y)

                if (cell.hasLeftWall()) {
                    g.strokeLine(x*ratioX, y*ratioY, x * ratioX, (y+1)*ratioY)
                }

                if (cell.hasTopWall()) {
                    g.strokeLine(x*ratioX, y*ratioY, (x+1) * ratioX, y*ratioY)
                }
            }
        }

        g.fill = Color.YELLOW
        g.fillOval(miniGame.startPoint.x, miniGame.startPoint.y, CHAR_SIZE, CHAR_SIZE)
        g.fillOval(miniGame.endPoint.x, miniGame.endPoint.y, CHAR_SIZE, CHAR_SIZE)

        oldPosition = charPosition
    }

    override fun onKeyPress(key: KeyCode) {
        miniGame.press(key)
    }
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CircuitBreakerMiniGame : MiniGame<CircuitBreakerResult>() {

    val maze = Maze(10, 10)

    val startPoint = Point2D(WIDTH / maze.width * 0.5, HEIGHT / maze.height * 0.5)
    val endPoint = Point2D(WIDTH - WIDTH / maze.width * 0.5, HEIGHT - HEIGHT / maze.height * 0.5)

    private var direction = Point2D(1.0, 0.0)

    var charPosition = startPoint
        private set

    private var t = 0.0

    override fun onUpdate(tpf: Double) {
        t += tpf

        if (t < 2.0) {
            return
        }

        charPosition = charPosition.add(direction.multiply(tpf * CHAR_SPEED))

        if (charPosition.x < 0 || charPosition.y < 0 ||
                charPosition.x > WIDTH || charPosition.y > HEIGHT) {
            isDone = true
            result = CircuitBreakerResult(false)
        }

        if (charPosition.distance(endPoint) < CHAR_SIZE) {
            isDone = true
            result = CircuitBreakerResult(true)
        }
    }

    fun press(key: KeyCode) {
        when (key) {
            KeyCode.W -> { direction = Point2D(0.0, -1.0) }
            KeyCode.S -> { direction = Point2D(0.0, 1.0) }
            KeyCode.A -> { direction = Point2D(-1.0, 0.0) }
            KeyCode.D -> { direction = Point2D(1.0, 0.0) }
            else -> {}
        }
    }
}

class CircuitBreakerResult(override val isSuccess: Boolean) : MiniGameResult
