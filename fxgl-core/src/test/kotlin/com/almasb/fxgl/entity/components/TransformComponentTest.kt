/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.serialization.Bundle
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.number.IsCloseTo.closeTo
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

    @Test
    fun `Rotations`() {
        val c = TransformComponent()

        assertThat(c.rotationX, `is`(0.0))
        assertThat(c.rotationY, `is`(0.0))
        assertThat(c.rotationZ, `is`(0.0))

        // rotate along X by -25.0
        c.lookDownBy(25.0)

        assertThat(c.rotationX, `is`(-25.0))
        assertThat(c.rotationY, `is`(0.0))
        assertThat(c.rotationZ, `is`(0.0))

        // rotate along Y by -35.0
        c.lookLeftBy(35.0)

        assertThat(c.rotationX, `is`(-25.0))
        assertThat(c.rotationY, `is`(-35.0))
        assertThat(c.rotationZ, `is`(0.0))

        // rotate along X by 10.0
        c.lookUpBy(10.0)

        assertThat(c.rotationX, `is`(-15.0))
        assertThat(c.rotationY, `is`(-35.0))
        assertThat(c.rotationZ, `is`(0.0))

        // rotate along Y by 50.0
        c.lookRightBy(50.0)

        assertThat(c.rotationX, `is`(-15.0))
        assertThat(c.rotationY, `is`(15.0))
        assertThat(c.rotationZ, `is`(0.0))

        c.rotationZ = 33.0

        assertThat(c.rotationX, `is`(-15.0))
        assertThat(c.rotationY, `is`(15.0))
        assertThat(c.rotationZ, `is`(33.0))
    }

    @Test
    fun `Look At`() {
        val c = TransformComponent()
        c.setPosition3D(10.0, 5.0, -3.0)

        c.lookAt(Point3D.ZERO)

        assertThat(c.direction3D, `is`(Point3D(-0.8850428765552463, -0.3990885011160071, 0.23964030323756916)))
        assertThat(c.rotationZ, `is`(0.0))
        assertThat(c.rotationX, closeTo(25.6, 0.1))
        assertThat(c.rotationY, closeTo(-73.3, 0.1))

        c.lookAt(Point3D(15.0, 3.5, 4.5))

        assertThat(c.direction3D, `is`(Point3D(0.5524917091340396, -0.16329323477878344, 0.8173666440549362)))
        assertThat(c.rotationZ, `is`(0.0))
        assertThat(c.rotationX, closeTo(9.45, 0.1))
        assertThat(c.rotationY, closeTo(33.69, 0.1))
    }

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