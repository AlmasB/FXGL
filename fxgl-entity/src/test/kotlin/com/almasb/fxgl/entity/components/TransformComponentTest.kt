/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.serialization.Bundle
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TransformComponentTest {

    @Test
    fun `Serialization`() {
        val c = TransformComponent(Point2D(30.0, 55.0))
        c.scaleOrigin = Point2D(1.0, 1.0)
        c.scaleX = 1.5
        c.scaleY = 3.0
        c.angle = 45.0
        c.rotationOrigin = Point2D(2.0, 2.0)

        val bundle = Bundle("test")
        c.write(bundle)

        val c2 = TransformComponent()
        c2.read(bundle)

        assertTrue(areEqual(c, c2))
    }

    @Test
    fun `To String`() {
        val c = TransformComponent(100.0, 100.0, 30.0, 2.0, 2.0)

        assertThat(c.toString(), `is`("Transform(100.0, 100.0, 30.0, 2.0, 2.0)"))
    }

    //
//    @Test
//    fun `Creation`() {
//        val rot1 = RotationComponent(35.0)
//
//        assertThat(rot1.value, `is`(35.0))
//    }
//
//    @Test
//    fun `Copy`() {
//        val rot1 = RotationComponent(35.0)
//        val rot2 = rot1.copy()
//
//        assertThat(rot2.value, `is`(35.0))
//        assertTrue(rot1 !== rot2)
//    }
//
//    @Test
//    fun `Rotation`() {
//        val rot1 = RotationComponent(35.0)
//
//        rot1.rotateBy(30.0)
//        assertThat(rot1.value, `is`(65.0))
//
//        rot1.rotateBy(-65.0)
//        assertThat(rot1.value, `is`(0.0))
//
//        rot1.rotateToVector(Point2D(-1.0, 0.0))
//        assertThat(rot1.value, `is`(180.0))
//
//        rot1.rotateToVector(Point2D(1.0, 0.0))
//        assertThat(rot1.value, `is`(0.0))
//
//        rot1.rotateToVector(Point2D(0.0, -1.0))
//        assertThat(rot1.value, `is`(-90.0))
//
//        rot1.rotateToVector(Point2D(0.0, 1.0))
//        assertThat(rot1.value, `is`(90.0))
//    }
//

    private fun areEqual(t1: TransformComponent, t2: TransformComponent): Boolean {
        return t1.x == t2.x && t1.y == t2.y
                && t1.scaleX == t2.scaleX && t1.scaleY == t2.scaleY && t1.scaleOrigin == t2.scaleOrigin
                && t1.angle == t2.angle && t1.rotationOrigin == t2.rotationOrigin
    }

//
//    private lateinit var position: PositionComponent
//
//    @BeforeEach
//    fun setUp() {
//        position = PositionComponent()
//    }
//
//    @Test
//    fun `Grid coordinates`() {
//        // mock entity
//        val e = Entity()
//        e.position = Point2D(55.0, 35.0)
//
//
//        assertAll(
//                Executable { assertThat(e.positionComponent.getGridX(25), `is`(2)) },
//                Executable { assertThat(e.positionComponent.getGridY(25), `is`(1)) }
//        )
//    }
//
//    @Test
//    fun `Translate X`() {
//        position.translateX(100.0)
//        assertThat(position.x, `is`(100.0))
//
//        position.translateX(100.0)
//        assertThat(position.x, `is`(200.0))
//
//        position.translateX(-250.0)
//        assertThat(position.x, `is`(-50.0))
//    }
//
//    @Test
//    fun `Translate Y`() {
//        position.translateY(100.0)
//        assertThat(position.y, `is`(100.0))
//
//        position.translateY(100.0)
//        assertThat(position.y, `is`(200.0))
//
//        position.translateY(-250.0)
//        assertThat(position.y, `is`(-50.0))
//    }
//
//    @Test
//    fun `Distance`() {
//        val position2 = PositionComponent()
//        assertThat(position.distance(position2), `is`(0.0))
//
//        position2.setValue(100.0, 0.0)
//        assertThat(position.distance(position2), `is`(100.0))
//
//        position2.setValue(0.0, 100.0)
//        assertThat(position.distance(position2), `is`(100.0))
//
//        position.setValue(25.0, 25.0)
//        position2.setValue(50.0, 50.0)
//        assertEquals(35.0, position.distance(position2), 0.5)
//    }
//
//    @Test
//    fun `Translate`() {
//        position.translate(100.0, 50.0)
//        assertThat(position.value, `is`(Point2D(100.0, 50.0)))
//
//        position.translate(-50.0, 30.0)
//        assertThat(position.value, `is`(Point2D(50.0, 80.0)))
//    }
//
//    @Test
//    fun `Translate towards`() {
//        position.translateTowards(Point2D(20.0, 0.0), 5.0)
//
//        assertThat(position.value, `is`(Point2D(5.0, 0.0)))
//    }
}