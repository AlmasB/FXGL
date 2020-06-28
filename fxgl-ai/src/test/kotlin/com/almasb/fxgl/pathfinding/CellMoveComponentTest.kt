/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding

import com.almasb.fxgl.entity.Entity
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CellMoveComponentTest {

    private lateinit var e: Entity
    private lateinit var cellMove: CellMoveComponent

    @BeforeEach
    fun setUp() {
        e = Entity()
        cellMove = CellMoveComponent(40, 40, 40 * 1.0)

        e.addComponent(cellMove)
    }

    @Test
    fun `Local anchor affects the positioning of entity inside the cell`() {
        e.localAnchor = Point2D(20.0, 20.0)

        cellMove.setPositionToCell(1, 1)

        assertThat(e.x, `is`(40.0))
        assertThat(e.y, `is`(40.0))

        e.x += 25.0

        assertThat(cellMove.cellX, `is`(2))
        assertThat(cellMove.cellY, `is`(1))
    }

    @Test
    fun `isAtDestination is only true when component has reached destination`() {
        assertTrue(cellMove.isAtDestination)

        assertThat(cellMove.cellX, `is`(0))
        assertThat(cellMove.cellY, `is`(0))
        assertThat(e.x, `is`(0.0))
        assertThat(e.y, `is`(0.0))

        cellMove.setPositionToCell(0, 0)

        assertTrue(cellMove.isAtDestination)
        assertFalse(cellMove.isMoving)

        assertThat(e.x, `is`(20.0))
        assertThat(e.y, `is`(20.0))

        // move right

        cellMove.moveToCell(1, 0)

        assertFalse(cellMove.isAtDestination)
        assertTrue(cellMove.isMoving)

        cellMove.onUpdate(0.016)
        assertThat(e.x, `is`(not(20.0)))
        assertThat(e.y, `is`(20.0))
        assertTrue(cellMove.isMovingRight)

        repeat(62) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(1))
        assertThat(cellMove.cellY, `is`(0))

        assertThat(e.x, `is`(60.0))
        assertThat(e.y, `is`(20.0))

        assertTrue(cellMove.isAtDestination)
        assertFalse(cellMove.isMoving)
        assertFalse(cellMove.isMovingRight)

        // move down

        cellMove.moveToCell(1, 2)

        repeat(62) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(1))
        assertThat(cellMove.cellY, `is`(1))

        assertFalse(cellMove.isAtDestination)
        assertTrue(cellMove.isMovingDown)

        repeat(65) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(1))
        assertThat(cellMove.cellY, `is`(2))

        assertTrue(cellMove.isAtDestination)
        assertFalse(cellMove.isMoving)
        assertFalse(cellMove.isMovingDown)
    }
}