/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MouseEventData(
        val event: MouseEvent,
        val viewportOrigin: Point2D,
        val scaleRatioX: Double,
        val scaleRatioY: Double
)