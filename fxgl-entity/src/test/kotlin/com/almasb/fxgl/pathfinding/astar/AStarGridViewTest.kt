/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.pathfinding.astar

import com.almasb.fxgl.pathfinding.CellState
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AStarGridViewTest {

    @Test
    fun `Grid view is correctly generated from astar grid`() {
        val grid = AStarGrid(20, 15)
        grid.get(10, 11).state = CellState.NOT_WALKABLE
        grid.get(12, 5).state = CellState.NOT_WALKABLE

        val view = AStarGridView(grid, 30, 25)

        val rectGroup = view.childrenUnmodifiable[0] as Group
        val linesGroup = view.childrenUnmodifiable[1] as Group
        val coordGroup = view.childrenUnmodifiable[2] as Group

        assertThat(rectGroup.children.size, `is`(20 * 15))
        assertThat(linesGroup.children.size, `is`(20 + 15))
        assertThat(coordGroup.children.size, `is`(20 * 15))

        val rect1 = rectGroup.children.find { it.translateX == 10*30.0 && it.translateY == 11*25.0 } as Rectangle
        val rect2 = rectGroup.children.find { it.translateX == 12*30.0 && it.translateY == 5*25.0 } as Rectangle

        val red = Color.color(0.8, 0.0, 0.0, 0.75)
        val green = Color.color(0.0, 0.8, 0.0, 0.75)

        assertThat(rect1.fill, `is`<Paint>(red))
        assertThat(rect2.fill, `is`<Paint>(red))

        rectGroup.children.minus(arrayOf(rect1, rect2)).forEach {
            assertThat((it as Rectangle).fill, `is`<Paint>(green))
        }
    }
}