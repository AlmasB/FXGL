/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.lighting

import javafx.geometry.Point2D

/**
 * Adapted from https://github.com/timyates/ShadowFX
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Intersection(val point: Point2D, val distance: Double) {

    companion object {
        val NONE = Intersection(Point2D(100.0, 100.0), java.lang.Double.MAX_VALUE)

        fun intersect(ray: Segment, segment: Segment): Intersection {
            val rayLength = ray.magnitude()
            val segmentLength = segment.magnitude()

            if (ray.to.x / rayLength == segment.to.x / segmentLength
                    && ray.to.y / rayLength == segment.to.y / segmentLength) { // Directions are the same.
                return NONE
            }

            val T2 = (ray.to.x * (segment.from.y - ray.from.y) + ray.to.y * (ray.from.x - segment.from.x)) /
                    (segment.to.x * ray.to.y - segment.to.y * ray.to.x)

            val T1 = (segment.from.x + segment.to.x * T2 - ray.from.x) / ray.to.x

            if (T1 < 0 || T2 < 0 || T2 > 1)
                return NONE

            return Intersection(
                    Point2D(ray.from.x + ray.to.x * T1, ray.from.y + ray.to.y * T1),
                    T1)
        }
    }
}