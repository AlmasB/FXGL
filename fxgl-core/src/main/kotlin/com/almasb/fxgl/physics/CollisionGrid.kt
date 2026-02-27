/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.math.FXGLMath.max
import com.almasb.fxgl.core.math.FXGLMath.min
import com.almasb.fxgl.entity.Entity
import javafx.geometry.Point2D

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class CollisionGrid(val cellWidth: Int, val cellHeight: Int) {

    val cells = hashMapOf<Point2D, CollisionCell>()

    fun insert(e: Entity) {
        if (e.boundingBoxComponent.hitBoxesProperty().isEmpty())
            return

        var box = e.boundingBoxComponent.hitBoxesProperty()[0]

        var minX = box.fastMinX
        var minY = box.fastMinY
        var maxX = box.fastMaxX
        var maxY = box.fastMaxY

        for (i in 1 until e.boundingBoxComponent.hitBoxesProperty().size) {
            box = e.boundingBoxComponent.hitBoxesProperty()[i]

            minX = min(minX, box.fastMinX)
            minY = min(minY, box.fastMinY)

            maxX = max(maxX, box.fastMaxX)
            maxY = max(maxY, box.fastMaxY)
        }

        val tl = Point2D(minX.toDouble(), minY.toDouble())
        val br = Point2D(maxX.toDouble(), maxY.toDouble())

        val tlX = Math.floor(tl.x / cellWidth).toInt()
        val tlY = Math.floor(tl.y / cellHeight).toInt()

        val brX = Math.ceil(br.x / cellWidth).toInt()
        val brY = Math.ceil(br.y / cellHeight).toInt()

        for (x in tlX..brX) {
            for (y in tlY..brY) {
                val point = Point2D(x.toDouble(), y.toDouble())

                var cell = cells[point]

                if (cell == null) {
                    cell = CollisionCell(x, y)
                    cells[point] = cell
                }

                cell.entities.add(e)
            }
        }
    }
}

internal class CollisionCell(val x: Int, val y: Int) {
    val entities = Array<Entity>()
}