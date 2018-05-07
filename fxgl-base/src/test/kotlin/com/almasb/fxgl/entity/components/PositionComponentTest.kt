/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.io.serialization.Bundle
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class PositionComponentTest {

    private lateinit var position: PositionComponent

    @BeforeEach
    fun setUp() {
        position = PositionComponent()
    }

    @Test
    fun `Grid coordinates`() {
        // mock entity
        val e = Entity()
        e.position = Point2D(55.0, 35.0)


        assertAll(
                Executable { assertThat(e.positionComponent.getGridX(25), `is`(2)) },
                Executable { assertThat(e.positionComponent.getGridY(25), `is`(1)) }
        )
    }

    @Test
    fun `Translate X`() {
        position.translateX(100.0)
        assertThat(position.x, `is`(100.0))

        position.translateX(100.0)
        assertThat(position.x, `is`(200.0))

        position.translateX(-250.0)
        assertThat(position.x, `is`(-50.0))
    }

    @Test
    fun `Translate Y`() {
        position.translateY(100.0)
        assertThat(position.y, `is`(100.0))

        position.translateY(100.0)
        assertThat(position.y, `is`(200.0))

        position.translateY(-250.0)
        assertThat(position.y, `is`(-50.0))
    }

    @Test
    fun `Distance`() {
        val position2 = PositionComponent()
        assertThat(position.distance(position2), `is`(0.0))

        position2.setValue(100.0, 0.0)
        assertThat(position.distance(position2), `is`(100.0))

        position2.setValue(0.0, 100.0)
        assertThat(position.distance(position2), `is`(100.0))

        position.setValue(25.0, 25.0)
        position2.setValue(50.0, 50.0)
        assertEquals(35.0, position.distance(position2), 0.5)
    }

    @Test
    fun `Translate`() {
        position.translate(100.0, 50.0)
        assertThat(position.value, `is`(Point2D(100.0, 50.0)))

        position.translate(-50.0, 30.0)
        assertThat(position.value, `is`(Point2D(50.0, 80.0)))
    }

    @Test
    fun `Translate towards`() {
        position.translateTowards(Point2D(20.0, 0.0), 5.0)

        assertThat(position.value, `is`(Point2D(5.0, 0.0)))
    }

    @Test
    fun `Copy`() {
        position = PositionComponent(Point2D(33.0, -33.0))
        val copy = position.copy()

        assertThat(copy.value, `is`(Point2D(33.0, -33.0)))
    }

    @Test
    fun `Save and load`() {
        position.value = Point2D(33.0, -33.0)

        val bundle = Bundle("Test")
        position.write(bundle)

        val position2 = PositionComponent()
        position2.read(bundle)

        assertThat(position2.value, `is`(Point2D(33.0, -33.0)))
    }
}