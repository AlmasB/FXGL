/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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