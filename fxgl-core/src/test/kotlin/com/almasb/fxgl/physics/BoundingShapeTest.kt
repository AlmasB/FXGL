/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import javafx.geometry.Point2D
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
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
        assertThat((shape as PolygonShapeData).points.toList(), contains(Point2D(0.0, 0.0), Point2D(1.0, 0.0), Point2D(1.0, 1.0), Point2D(0.0, 1.0)))
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