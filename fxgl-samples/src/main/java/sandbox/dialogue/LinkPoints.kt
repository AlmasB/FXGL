/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import javafx.scene.Group
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
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


//        background = null
//
//        children.add(circle)
    }
}

class InLinkPoint : LinkPoint() {}

class OutLinkPoint : LinkPoint() {}