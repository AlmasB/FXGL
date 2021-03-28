/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.pathfinding

import com.almasb.fxgl.core.collection.grid.Cell
import com.almasb.fxgl.entity.Entity
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
        val finishCell = object : Cell(0,0) {}

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

        cellMove.moveToCell(2, 0)

        assertFalse(cellMove.isAtDestination)
        assertTrue(cellMove.isMoving)

        repeat(62) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.x, `is`(not(20.0)))
        assertThat(e.y, `is`(20.0))
        assertTrue(cellMove.isMovingRight)
        assertThat(e.rotation, `is`(0.0))

        repeat(65) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(2))
        assertThat(cellMove.cellY, `is`(0))

        assertTrue(cellMove.isAtDestination)
        assertFalse(cellMove.isMoving)
        assertFalse(cellMove.isMovingRight)
        assertThat(e.rotation, `is`(0.0))

        // move down

        cellMove.moveToCell(2, 2)

        repeat(62) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(2))
        assertThat(cellMove.cellY, `is`(1))

        assertFalse(cellMove.isAtDestination)
        assertTrue(cellMove.isMovingDown)
        assertThat(e.rotation, `is`(0.0))

        repeat(65) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(2))
        assertThat(cellMove.cellY, `is`(2))

        assertTrue(cellMove.isAtDestination)
        assertFalse(cellMove.isMoving)
        assertFalse(cellMove.isMovingDown)
        assertThat(e.rotation, `is`(0.0))

        // move left

        cellMove.moveToCell(0, 2)

        repeat(62) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(1))
        assertThat(cellMove.cellY, `is`(2))

        assertFalse(cellMove.isAtDestination)
        assertTrue(cellMove.isMovingLeft)
        assertThat(e.rotation, `is`(0.0))

        repeat(65) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(0))
        assertThat(cellMove.cellY, `is`(2))

        assertTrue(cellMove.isAtDestination)
        assertFalse(cellMove.isMoving)
        assertFalse(cellMove.isMovingLeft)
        assertThat(e.rotation, `is`(0.0))

        // move up

        cellMove.moveToCell(finishCell)

        repeat(62) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(0))
        assertThat(cellMove.cellY, `is`(1))

        assertFalse(cellMove.isAtDestination)
        assertTrue(cellMove.isMovingUp)
        assertThat(e.rotation, `is`(0.0))

        repeat(65) {
            cellMove.onUpdate(0.016)
        }

        assertThat(cellMove.cellX, `is`(0))
        assertThat(cellMove.cellY, `is`(0))

        assertTrue(cellMove.isAtDestination)
        assertFalse(cellMove.isMoving)
        assertFalse(cellMove.isMovingUp)
        assertThat(e.rotation, `is`(0.0))
    }

    @Test
    fun `Component rotates while moving if its allowed`(){
        val firstCell = object : Cell(0,0) {}
        val secondCell = object : Cell(1,0) {}
        val thirdCell = object : Cell(1,1) {}
        val fourthCell = object : Cell(0,1) {}
        cellMove.allowRotation(true)

        cellMove.setPositionToCell(firstCell)

        assertThat(e.rotation, `is`(0.0))

        // Move right

        cellMove.moveToCell(secondCell)

        repeat(30) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.rotation, `is`(0.0))

        repeat(35) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.rotation, `is`(0.0))
        assertFalse(cellMove.isMoving)
        assertTrue(cellMove.atDestinationProperty().value)

        // Move down

        cellMove.moveToCell(thirdCell)

        repeat(30) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.rotation, `is`(90.0))

        repeat(35) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.rotation, `is`(90.0))
        assertFalse(cellMove.isMoving)
        assertTrue(cellMove.atDestinationProperty().value)

        // Move down

        cellMove.moveToCell(fourthCell)

        repeat(30) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.rotation, `is`(180.0))

        repeat(35) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.rotation, `is`(180.0))
        assertFalse(cellMove.isMoving)
        assertTrue(cellMove.atDestinationProperty().value)

        // Move up

        cellMove.moveToCell(firstCell)

        repeat(30) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.rotation, `is`(270.0))

        repeat(35) {
            cellMove.onUpdate(0.016)
        }

        assertThat(e.rotation, `is`(270.0))
        assertFalse(cellMove.isMoving)
        assertTrue(cellMove.atDestinationProperty().value)
    }

    @Test
    fun `CellMovementComponent width, height and speed are changeable after component creation`() {

        assertThat(cellMove.cellHeight, `is`(40))
        assertThat(cellMove.cellWidth, `is`(40))
        assertThat(cellMove.speed, `is`(40.0))

        cellMove.cellHeight = 20
        cellMove.cellWidth = 20
        cellMove.speed = 20.0

        assertThat(cellMove.cellHeight, `is`(20))
        assertThat(cellMove.cellWidth, `is`(20))
        assertThat(cellMove.speed, `is`(20.0))
    }
}