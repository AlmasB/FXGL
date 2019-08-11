/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class StartNodeView : NodeView(StartNode("Hello Start")) {

    val outLink = OutLinkPoint()

    init {
        addOutPoint(outLink)
    }
}