/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.circuitbreaker

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.minigames.MiniGameView
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.util.Duration
import kotlin.math.floor

private const val WIDTH = 700.0
private const val HEIGHT = 500.0

class CircuitBreakerView(miniGame: CircuitBreakerMiniGame = CircuitBreakerMiniGame(10, 10, 15.0, 135.0, Duration.seconds(2.0)))
    : MiniGameView<CircuitBreakerMiniGame>(miniGame) {

    private val canvas = Canvas(WIDTH, HEIGHT)
    private val g = canvas.graphicsContext2D

    private var oldPosition = miniGame.startPoint

    private val lineData = arrayListOf<Pair<Point2D, Point2D>>()

    init {
        children += canvas
    }

    override fun onUpdate(tpf: Double) {
        val charPosition = miniGame.playerPosition

        g.clearRect(0.0, 0.0, WIDTH, HEIGHT)

        g.fill = Color.DARKGREEN
        g.globalAlpha = 0.9
        g.fillRect(0.0, 0.0, WIDTH, HEIGHT)

        g.stroke = Color.LIGHTGREEN
        g.globalAlpha = 1.0
        g.lineWidth = 6.5
        g.strokeRect(0.0, 0.0, WIDTH, HEIGHT)

        g.fill = Color.BLACK
        g.fillOval(charPosition.x, charPosition.y, miniGame.playerSize, miniGame.playerSize)

        lineData += oldPosition.add(miniGame.playerSize / 2, miniGame.playerSize / 2) to charPosition.add(miniGame.playerSize / 2, miniGame.playerSize / 2)

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

                if (cell.hasLeftWall) {
                    g.strokeLine(x*ratioX, y*ratioY, x * ratioX, (y+1)*ratioY)
                }

                if (cell.hasTopWall) {
                    g.strokeLine(x*ratioX, y*ratioY, (x+1) * ratioX, y*ratioY)
                }
            }
        }

        g.fill = Color.YELLOW
        g.fillOval(miniGame.startPoint.x, miniGame.startPoint.y, miniGame.playerSize, miniGame.playerSize)
        g.fillOval(miniGame.endPoint.x, miniGame.endPoint.y, miniGame.playerSize, miniGame.playerSize)

        oldPosition = charPosition
    }

    override fun onInitInput(input: Input) {
        input.addAction(object : UserAction("Up") {
            override fun onAction() {
                miniGame.up()
            }
        }, KeyCode.W)

        input.addAction(object : UserAction("Down") {
            override fun onAction() {
                miniGame.down()
            }
        }, KeyCode.S)

        input.addAction(object : UserAction("Left") {
            override fun onAction() {
                miniGame.left()
            }
        }, KeyCode.A)

        input.addAction(object : UserAction("Right") {
            override fun onAction() {
                miniGame.right()
            }
        }, KeyCode.D)
    }
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CircuitBreakerMiniGame(mazeWidth: Int, mazeHeight: Int, 
                             val playerSize: Double, 
                             val playerSpeed: Double,
                             private val initialDelay: Duration) : MiniGame<CircuitBreakerResult>() {

    val maze = Maze(mazeWidth, mazeHeight)

    val startPoint = Point2D(WIDTH / maze.width * 0.5, HEIGHT / maze.height * 0.5)
    val endPoint = Point2D(WIDTH - WIDTH / maze.width * 0.5, HEIGHT - HEIGHT / maze.height * 0.5)

    private val DIR_UP = Point2D(0.0, -1.0)
    private val DIR_DOWN = Point2D(0.0, 1.0)
    private val DIR_LEFT = Point2D(-1.0, 0.0)
    private val DIR_RIGHT = Point2D(1.0, 0.0)

    private var direction = Point2D(1.0, 0.0)

    var playerPosition = startPoint
        private set

    private var t = 0.0

    override fun onUpdate(tpf: Double) {
        t += tpf

        if (t < initialDelay.toSeconds()) {
            return
        }

        playerPosition = playerPosition.add(direction.multiply(tpf * playerSpeed))

        if (isPlayerCollidingWithBounds() || isPlayerCollidingWithWalls()) {
            isDone = true
            result = CircuitBreakerResult(false)
        }

        if (playerPosition.distance(endPoint) < playerSize) {
            isDone = true
            result = CircuitBreakerResult(true)
        }
    }

    private fun isPlayerCollidingWithBounds(): Boolean {
        return playerPosition.x < 0 || playerPosition.y < 0 ||
                playerPosition.x + playerSize > WIDTH || playerPosition.y + playerSize > HEIGHT
    }

    private fun isPlayerCollidingWithWalls(): Boolean {
        val tl = getMazeCell(playerPosition)
        val tr = getMazeCell(playerPosition.add(playerSize, 0.0))
        val bl = getMazeCell(playerPosition.add(0.0, playerSize))
        val br = getMazeCell(playerPosition.add(playerSize, playerSize))

        if (tl !== tr && tr.hasLeftWall)
            return true

        if (tl !== bl && bl.hasTopWall)
            return true

        if (tr !== br && br.hasTopWall)
            return true

        if (bl !== br && br.hasLeftWall)
            return true

        return false
    }

    private fun getMazeCell(p: Point2D): MazeCell {
        val x = floor(FXGLMath.map(p.x, 0.0, WIDTH, 0.0, maze.width.toDouble())).toInt()
        val y = floor(FXGLMath.map(p.y, 0.0, HEIGHT, 0.0, maze.height.toDouble())).toInt()

        check (x < maze.width && y < maze.height) {
            "Bug: point $p is at ($x,$y) is outside of the maze (${maze.width},${maze.height})"
        }

        return maze.getMazeCell(x, y)
    }

    fun up() {
        if (direction !== DIR_DOWN)
            direction = DIR_UP
    }

    fun down() {
        if (direction !== DIR_UP)
            direction = DIR_DOWN
    }

    fun left() {
        if (direction !== DIR_RIGHT)
            direction = DIR_LEFT
    }

    fun right() {
        if (direction !== DIR_LEFT)
            direction = DIR_RIGHT
    }
}

class CircuitBreakerResult(override val isSuccess: Boolean) : MiniGameResult
