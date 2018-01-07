/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import com.almasb.fxgl.app.FXGL
import javafx.scene.paint.Color

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EndNodeView : NodeView() {

    init {

        val text = FXGL.getUIFactory().newText("END", Color.BLACK, 34.0)


        contentPane.children.add(text)

        val inLink = InLinkPoint()
        inLink.translateX = -45.0
        inLink.translateY = 30.0

        children.add(inLink)

        inPoints.add(inLink)
    }
}