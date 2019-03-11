/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.entity.Entity
import javafx.geometry.Point2D
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class BoundingShapeTest {

    @ParameterizedTest
    @MethodSource("dataProvider")
    fun `Polygon shape`(shape: BoundingShape) {
        val dataArray = shape.data as Array<Point2D>

        assertThat(dataArray.toList(), contains(Point2D(0.0, 0.0), Point2D(1.0, 0.0), Point2D(1.0, 1.0), Point2D(0.0, 1.0)))
    }

    @Test
    fun `Polygon shape throws if too few points`() {
        assertThrows<IllegalArgumentException> {
            BoundingShape.polygon(0.0, 0.0)
        }
    }

    @Test
    fun `Chain shape throws if too few points`() {
        assertThrows<IllegalArgumentException> {
            BoundingShape.chain(Point2D(0.0, 0.0))
        }
    }

    companion object {
        @JvmStatic fun dataProvider(): Stream<BoundingShape> {
            return Stream.of(
                    BoundingShape.polygon(Point2D(0.0, 0.0), Point2D(1.0, 0.0), Point2D(1.0, 1.0), Point2D(0.0, 1.0)),
                    BoundingShape.polygon(0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0),
                    BoundingShape.polygon(listOf(Point2D(0.0, 0.0), Point2D(1.0, 0.0), Point2D(1.0, 1.0), Point2D(0.0, 1.0))),
                    BoundingShape.polygonFromDoubles(listOf(0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0))
            )
        }
    }
}