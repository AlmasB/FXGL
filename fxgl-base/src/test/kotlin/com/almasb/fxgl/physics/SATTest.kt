/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import javafx.beans.property.SimpleDoubleProperty
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
    fun `AABB`() {
        var box1 = HitBox("main", BoundingShape.box(20.0, 40.0))
        var box2 = HitBox("main", BoundingShape.box(20.0, 40.0))

        assertTrue(SAT.isColliding(box1, box2, 0.0, 0.0))

        box1 = HitBox("main", BoundingShape.box(20.0, 40.0))
        box2 = HitBox("main", BoundingShape.box(20.0, 40.0))

        box2.bindX(SimpleDoubleProperty(21.0))

        assertFalse(SAT.isColliding(box1, box2, 0.0, 0.0))
    }

    // https://github.com/AlmasB/FXGL/issues/490
//    @Test
//    fun `HitBoxes with angles`() {
//        val box1 = HitBox("main", BoundingShape.box(20.0, 40.0))
//        val box2 = HitBox("main", BoundingShape.box(20.0, 40.0))
//
//        box2.bindX(SimpleDoubleProperty(21.0))
//
//        assertTrue(SAT.isColliding(box1, box2, 0.0, 90.0))
//    }
}