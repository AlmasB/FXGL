/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.FXGL
import javafx.scene.paint.Color

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EndNodeView : NodeView(EndNode("")) {

    init {

        val inLink = InLinkPoint()
        addInPoint(inLink)

        val text = FXGL.getUIFactory().newText("END", Color.WHITE, 24.0)


        //children.add(text)


    }
}