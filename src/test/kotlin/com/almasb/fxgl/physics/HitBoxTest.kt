/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.physics

import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
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

    @Test
    fun `Test serialization`() {
        val hitbox = HitBox("Test", Point2D(10.0, 10.0), BoundingShape.box(30.0, 30.0))

        val baos = ByteArrayOutputStream()

        ObjectOutputStream(baos).use {
            it.writeObject(hitbox)
        }

        val hitbox2 = ObjectInputStream(ByteArrayInputStream(baos.toByteArray())).use {
            it.readObject()
        } as HitBox

        assertThat(hitbox.name, `is`(hitbox2.name))
        assertThat(hitbox.bounds, `is`(hitbox2.bounds))
        assertThat(hitbox.shape.type, `is`(hitbox2.shape.type))
    }
}