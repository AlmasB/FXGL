/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CellTest {

    @Test
    fun `User data`() {
        val cell = object : Cell(0, 0) {}

        assertNull(cell.userData)

        cell.userData = "Hi"
        assertThat(cell.userData as String, `is`("Hi"))
    }

    @Test
    fun `distance`() {
        val cell1 = object : Cell(0, 0) {}
        val cell2 = object : Cell(2, 2) {}

        assertThat(cell1.distance(cell2), `is`(4))
    }

    @Test
    fun `To String`() {
        val cell = object : Cell(0, 0) {}

        assertThat(cell.toString(), `is`("Cell(0,0)"))
    }
}