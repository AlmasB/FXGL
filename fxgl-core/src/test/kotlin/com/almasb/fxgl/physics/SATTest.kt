/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.entity.components.TransformComponent
import javafx.geometry.Point2D
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SATTest {

    @Test
    fun `HitBoxes with scale`() {
        val box1 = HitBox(BoundingShape.box(20.0, 40.0))
        val box2 = HitBox(BoundingShape.box(20.0, 40.0))

        val t1 = TransformComponent(0.0, 0.0, 0.0, 1.0, 1.0)
        val t2 = TransformComponent(21.0, 0.0, 0.0, 1.0, 1.0)

        box1.applyTransform(t1)
        box2.applyTransform(t2)

        assertFalse(SAT.isColliding(box1, box2, 0.0, 0.0, t1, t2))

        t2.scaleOrigin = Point2D(10.0, 20.0)
        t2.scaleX = 1.2

        box1.applyTransform(t1)
        box2.applyTransform(t2)

        assertTrue(SAT.isColliding(box1, box2, 0.0, 0.0, t1, t2))
    }

    @Test
    fun `HitBoxes with angles`() {
        val box1 = HitBox("main", BoundingShape.box(20.0, 40.0))
        val box2 = HitBox("main", BoundingShape.box(20.0, 40.0))

        val t1 = TransformComponent(0.0, 0.0, 0.0, 1.0, 1.0)
        val t2 = TransformComponent(21.0, 0.0, 0.0, 1.0, 1.0)

        box1.applyTransform(t1)
        box2.applyTransform(t2)

        // 1 is to the left of 2, no collision
        assertFalse(SAT.isColliding(box1, box2, 0.0, 00.0, t1, t2))

        t2.rotationOrigin = Point2D(0.0, 0.0)
        t2.angle = 90.0

        box1.applyTransform(t1)
        box2.applyTransform(t2)

        // 2 is rotated and overlaps 1, collision
        assertTrue(SAT.isColliding(box1, box2, 0.0, 00.0, t1, t2))

        t1.y = 21.0

        box1.applyTransform(t1)
        box2.applyTransform(t2)

        // 1 is below 2, no collision
        assertFalse(SAT.isColliding(box1, box2, 0.0, 00.0, t1, t2))

        t1.rotationOrigin = Point2D(0.0, 0.0)
        t1.angle = -35.0

        box1.applyTransform(t1)
        box2.applyTransform(t2)

        // 1 is rotated back and overlaps 2, collision
        assertTrue(SAT.isColliding(box1, box2, 0.0, 00.0, t1, t2))
    }
}