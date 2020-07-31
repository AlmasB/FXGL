/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D

object Distances {

    // Adapted from https://stackoverflow.com/questions/4978323/how-to-calculate-distance-between-two-rectangles-context-a-game-in-lua
    fun distance(rect1: Rectangle2D, rect2: Rectangle2D): Double {
        val left = rect2.maxX < rect1.minX
        val right = rect2.minX > rect1.maxX
        val top = rect2.maxY < rect1.minY
        val bot = rect2.minY > rect1.maxY

        return when {
            (top && left) -> rect2.botRight().distance(rect1.topLeft())
            (top && right) -> rect2.botLeft().distance(rect1.topRight())
            (bot && left) -> rect2.topRight().distance(rect1.botLeft())
            (bot && right) -> rect2.topLeft().distance(rect1.botRight())
            top -> rect1.minY - rect2.maxY
            bot -> rect2.minY - rect1.maxY
            left -> rect1.minX - rect2.maxX
            right -> rect2.minX - rect1.maxX
            else -> 0.0
        }
    }
}

private fun Rectangle2D.botRight() = Point2D(this.maxX, this.maxY)
private fun Rectangle2D.botLeft() = Point2D(this.minX, this.maxY)
private fun Rectangle2D.topRight() = Point2D(this.maxX, this.minY)
private fun Rectangle2D.topLeft() = Point2D(this.minX, this.minY)