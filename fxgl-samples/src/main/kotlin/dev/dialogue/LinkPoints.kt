/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import javafx.scene.paint.Color
import javafx.scene.shape.Circle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class LinkPoint : Circle(8.0, 8.0, 8.0) {
    init {
        fill = Color.TRANSPARENT
        stroke = Color.YELLOW
        strokeWidth = 2.0
    }
}

class InLinkPoint : LinkPoint() {}

class OutLinkPoint : LinkPoint() {

    var choiceLocalID: Int = -1
}