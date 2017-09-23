/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class HitBoxTest {

    @Test
    fun `Test centers`() {
        // box shape
        var hitbox = com.almasb.fxgl.physics.HitBox("Test", com.almasb.fxgl.physics.BoundingShape.box(30.0, 30.0))

        assertThat(hitbox.centerLocal(), `is`(Point2D(15.0, 15.0)))
        assertThat(hitbox.centerWorld(0.0, 0.0), `is`(Point2D(15.0, 15.0)))

        var hitbox2 = com.almasb.fxgl.physics.HitBox("Test", Point2D(10.0, 50.0), com.almasb.fxgl.physics.BoundingShape.box(30.0, 30.0))

        assertThat(hitbox2.centerLocal(), `is`(Point2D(25.0, 65.0)))
        assertThat(hitbox2.centerWorld(0.0, 0.0), `is`(Point2D(25.0, 65.0)))

        // circle shape
        hitbox = com.almasb.fxgl.physics.HitBox("Test", com.almasb.fxgl.physics.BoundingShape.circle(15.0))

        assertThat(hitbox.centerLocal(), `is`(Point2D(15.0, 15.0)))
        assertThat(hitbox.centerWorld(0.0, 0.0), `is`(Point2D(15.0, 15.0)))

        hitbox2 = com.almasb.fxgl.physics.HitBox("Test", Point2D(10.0, 50.0), com.almasb.fxgl.physics.BoundingShape.circle(15.0))

        assertThat(hitbox2.centerLocal(), `is`(Point2D(25.0, 65.0)))
        assertThat(hitbox2.centerWorld(0.0, 0.0), `is`(Point2D(25.0, 65.0)))

        // chain shape
        hitbox = com.almasb.fxgl.physics.HitBox("Test", com.almasb.fxgl.physics.BoundingShape.chain(Point2D(0.0, 0.0), Point2D(30.0, 0.0), Point2D(30.0, 30.0), Point2D(0.0, 30.0)))

        assertThat(hitbox.centerLocal(), `is`(Point2D(15.0, 15.0)))
        assertThat(hitbox.centerWorld(0.0, 0.0), `is`(Point2D(15.0, 15.0)))

        hitbox2 = com.almasb.fxgl.physics.HitBox("Test", Point2D(10.0, 50.0), com.almasb.fxgl.physics.BoundingShape.chain(Point2D(0.0, 0.0), Point2D(30.0, 0.0), Point2D(30.0, 30.0), Point2D(0.0, 30.0)))

        assertThat(hitbox2.centerLocal(), `is`(Point2D(25.0, 65.0)))
        assertThat(hitbox2.centerWorld(0.0, 0.0), `is`(Point2D(25.0, 65.0)))
    }

    @Test
    fun `Test serialization`() {
        val hitbox = com.almasb.fxgl.physics.HitBox("Test", Point2D(10.0, 10.0), com.almasb.fxgl.physics.BoundingShape.box(30.0, 30.0))

        val baos = ByteArrayOutputStream()

        ObjectOutputStream(baos).use {
            it.writeObject(hitbox)
        }

        val hitbox2 = ObjectInputStream(ByteArrayInputStream(baos.toByteArray())).use {
            it.readObject()
        } as com.almasb.fxgl.physics.HitBox

        assertThat(hitbox.name, `is`(hitbox2.name))
        assertThat(hitbox.bounds, `is`(hitbox2.bounds))
        assertThat(hitbox.shape.type, `is`(hitbox2.shape.type))
    }
}