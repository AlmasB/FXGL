/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import javafx.geometry.Rectangle2D
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.number.IsCloseTo.closeTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DistancesTest {

    @ParameterizedTest
    @MethodSource("dataValueProvider")
    fun `Distance between two rectangles`(rect1: Rectangle2D, rect2: Rectangle2D, dist: Double) {
        assertThat(Distances.distance(rect1, rect2), closeTo(dist, 0.01))
    }

    companion object {
        @JvmStatic fun dataValueProvider(): Stream<Arguments> {

            // arguments are in order: rect1, rect2, distance between them
            return Stream.of(
                    // overlapping
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(0.0, 5.0, 5.0, 10.0), 0.0),

                    // top
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(0.0, -15.0, 10.0, 10.0), 5.0),

                    // bot
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(0.0, 15.0, 10.0, 10.0), 5.0),

                    // right
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(15.0, 0.0, 10.0, 10.0), 5.0),

                    // top right
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(15.0, -20.0, 10.0, 10.0), 11.18),

                    // bot right
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(15.0, 20.0, 10.0, 10.0), 11.18),

                    // left
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(-15.0, 0.0, 10.0, 10.0), 5.0),

                    // top left
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(-15.0, -20.0, 10.0, 10.0), 11.18),

                    // bot left
                    arguments(Rectangle2D(0.0, 0.0, 10.0, 10.0), Rectangle2D(-15.0, 20.0, 10.0, 10.0), 11.18)
            )
        }
    }
}