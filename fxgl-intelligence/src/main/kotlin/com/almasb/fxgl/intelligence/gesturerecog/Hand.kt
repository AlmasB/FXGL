/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.gesturerecog

import javafx.geometry.Point2D
import javafx.geometry.Point3D

/**
 * Each hand has an id and a list of 21 hand landmarks points.
 * Each point can be mapped into 2D app (or screen where appropriate) space via:
 * appX = (1 - pointX) * appWidth
 * appY = pointY * appHeight
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
data class Hand(
        val id: Int,
        val points: List<Point3D>
) {

    fun getPoint(landmark: HandLandmark) = points[landmark.ordinal]

    fun getScaledPoint2D(landmark: HandLandmark, scaleWidth: Double, scaleHeight: Double): Point2D {
        val p = points[landmark.ordinal]

        return Point2D((1.0 - p.x) * scaleWidth, p.y * scaleHeight)
    }
}
