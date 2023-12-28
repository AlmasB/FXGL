/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues.ui

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class StartNodeIcon : Pane() {

    init {
        val vBar = Rectangle(3.0, 40.0, Color.WHITE)
        vBar.stroke = Color.BLACK
        vBar.arcWidth = 7.0
        vBar.arcHeight = 7.0

        val triangle = Polygon(2.0, 5.0, 40.0, 10.0, 2.0, 22.0)
        triangle.fill = Color.WHITE
        triangle.stroke = Color.BLACK

        translateY = -vBar.height

        children.addAll(triangle, vBar)
    }
}