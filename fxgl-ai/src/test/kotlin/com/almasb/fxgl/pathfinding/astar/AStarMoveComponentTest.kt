@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.pathfinding.astar

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.pathfinding.CellMoveComponent
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AStarMoveComponentTest {
  private lateinit var e: Entity
  private lateinit var grid: AStarGrid
  private lateinit var aStarMoveComponent: AStarMoveComponent
  private lateinit var cellMoveComponent: CellMoveComponent

  @BeforeEach
  fun setUp() {
    grid = AStarGrid(GRID_SIZE, GRID_SIZE)
    grid.cellHeight
    cellMoveComponent = CellMoveComponent(40, 40, 40 * 1.0)
    aStarMoveComponent = AStarMoveComponent(grid)

    e = Entity()
    e.addComponent(cellMoveComponent)
    e.addComponent(aStarMoveComponent)
  }

  @Test
  fun `Component can be stopped while moving`() {
    assertComponentIsNotInMotionAt(0, 0)

    val (destX, destY) = 3 to 3
    putComponentInMotion(destX, destY)

    finishMotion()

    assertComponentIsNotInMotionAt(destX, destY)

    val (destX2, destY2) = 0 to 0
    putComponentInMotion(destX2, destY2)

    aStarMoveComponent.stopMovement()
    assertComponentIsNotInMotionAt(destX, destY)
  }

  //region Private Helpers
  private fun putComponentInMotion(x: Int, y: Int) {
    aStarMoveComponent.moveToCell(x, y)
    aStarMoveComponent.onUpdate(STEP_SIZE)
    assertFalse(aStarMoveComponent.isAtDestination)
    assertTrue(aStarMoveComponent.isMoving)
    assertFalse(aStarMoveComponent.isPathEmpty)
  }

  private fun finishMotion() {
    do {
      cellMoveComponent.onUpdate(STEP_SIZE)
      aStarMoveComponent.onUpdate(STEP_SIZE)
    } while (!aStarMoveComponent.isAtDestination)
  }

  private fun assertComponentIsNotInMotionAt(x: Int, y: Int) {
    assertThat(cellMoveComponent.cellX, `is`(x))
    assertThat(cellMoveComponent.cellY, `is`(y))
    assertTrue(aStarMoveComponent.isAtDestination)
    assertFalse(aStarMoveComponent.isMoving)
    assertTrue(aStarMoveComponent.isPathEmpty)
  }
  //endregion


  companion object {
    private const val GRID_SIZE = 20
    private const val STEP_SIZE = 0.016
  }
}