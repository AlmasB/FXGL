/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class LinkPoint : Pane() {
    init {
        val circle = Circle(10.0, 10.0, 10.0)
        with(circle) {
            fill = null
            stroke = Color.BLUE
            strokeWidth = 2.0
        }

        children.add(circle)
    }
}

class InLinkPoint : LinkPoint() {}

class OutLinkPoint : LinkPoint() {}