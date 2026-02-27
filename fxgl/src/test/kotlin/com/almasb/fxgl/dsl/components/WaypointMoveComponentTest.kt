/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WaypointMoveComponentTest {

    @Test
    fun `Moving via WaypointComponent`() {
        val comp = WaypointMoveComponent(100.0, listOf(Point2D(20.0, 50.0), Point2D(40.0, 50.0)))

        val e = Entity()
        e.addComponent(comp)

        assertFalse(comp.atDestinationProperty().value)

        comp.onUpdate(5.0)
        assertThat(e.position, `is`(Point2D(20.0, 50.0)))

        assertFalse(comp.atDestinationProperty().value)

        comp.onUpdate(5.0)
        assertThat(e.position, `is`(Point2D(40.0, 50.0)))

        assertTrue(comp.atDestinationProperty().value)
    }
}