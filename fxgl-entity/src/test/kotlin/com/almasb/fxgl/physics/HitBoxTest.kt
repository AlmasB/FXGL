/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class HitBoxTest {

    @Test
    fun `Test centers`() {
        // box shape
        var hitbox = HitBox("Test", BoundingShape.box(30.0, 30.0))

        assertThat(hitbox.centerLocal(), `is`(Point2D(15.0, 15.0)))
        assertThat(hitbox.centerWorld(0.0, 0.0), `is`(Point2D(15.0, 15.0)))

        var hitbox2 = HitBox("Test", Point2D(10.0, 50.0), BoundingShape.box(30.0, 30.0))

        assertThat(hitbox2.centerLocal(), `is`(Point2D(25.0, 65.0)))
        assertThat(hitbox2.centerWorld(0.0, 0.0), `is`(Point2D(25.0, 65.0)))

        // circle shape
        hitbox = HitBox("Test", BoundingShape.circle(15.0))

        assertThat(hitbox.centerLocal(), `is`(Point2D(15.0, 15.0)))
        assertThat(hitbox.centerWorld(0.0, 0.0), `is`(Point2D(15.0, 15.0)))

        hitbox2 = HitBox("Test", Point2D(10.0, 50.0), BoundingShape.circle(15.0))

        assertThat(hitbox2.centerLocal(), `is`(Point2D(25.0, 65.0)))
        assertThat(hitbox2.centerWorld(0.0, 0.0), `is`(Point2D(25.0, 65.0)))

        // chain shape
        hitbox = HitBox("Test", BoundingShape.chain(Point2D(0.0, 0.0), Point2D(30.0, 0.0), Point2D(30.0, 30.0), Point2D(0.0, 30.0)))

        assertThat(hitbox.centerLocal(), `is`(Point2D(15.0, 15.0)))
        assertThat(hitbox.centerWorld(0.0, 0.0), `is`(Point2D(15.0, 15.0)))

        hitbox2 = HitBox("Test", Point2D(10.0, 50.0), BoundingShape.chain(Point2D(0.0, 0.0), Point2D(30.0, 0.0), Point2D(30.0, 30.0), Point2D(0.0, 30.0)))

        assertThat(hitbox2.centerLocal(), `is`(Point2D(25.0, 65.0)))
        assertThat(hitbox2.centerWorld(0.0, 0.0), `is`(Point2D(25.0, 65.0)))
    }

    @ParameterizedTest
    @MethodSource("shapeProvider")
    fun `Test serialization`(shape: BoundingShape) {
        val hitbox = HitBox("Test", Point2D(10.0, 10.0), shape)

        val baos = ByteArrayOutputStream()

        ObjectOutputStream(baos).use {
            it.writeObject(hitbox)
        }

        val hitbox2 = ObjectInputStream(baos.toByteArray().inputStream()).use {
            it.readObject()
        } as HitBox

        assertThat(hitbox.name, `is`(hitbox2.name))
        assertThat(hitbox.bounds, `is`(hitbox2.bounds))
        assertThat(hitbox.shape.type, `is`(hitbox2.shape.type))
    }

    companion object {
        @JvmStatic fun shapeProvider(): Stream<BoundingShape> {
            return Stream.of(
                    BoundingShape.box(30.0, 30.0),
                    BoundingShape.circle(15.0),
                    BoundingShape.polygon(
                            Point2D(0.0, 0.0),
                            Point2D(3.0, 0.0),
                            Point2D(3.0, 3.0),
                            Point2D(0.0, 3.0)
                    ),
                    BoundingShape.chain(
                            Point2D(0.0, 0.0),
                            Point2D(3.0, 0.0),
                            Point2D(3.0, 3.0),
                            Point2D(0.0, 3.0)
                    )
            )
        }
    }

    @Test
    fun `To String`() {
        val box = HitBox("Test", Point2D(10.0, 10.0), BoundingShape.box(30.0, 30.0))

        assertThat(box.toString(), `is`("HitBox(Test,POLYGON)"))

        val box2 = HitBox(Point2D(10.0, 10.0), BoundingShape.chain(
                Point2D(0.0, 0.0),
                Point2D(3.0, 0.0),
                Point2D(3.0, 3.0),
                Point2D(0.0, 3.0)
        ))

        assertTrue(box2.toString().endsWith("CHAIN)"))
    }
}