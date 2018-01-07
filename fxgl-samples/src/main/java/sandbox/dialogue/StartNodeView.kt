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
class StartNodeView : NodeView() {

    val outLink = OutLinkPoint()

    init {

        val text = FXGL.getUIFactory().newText("START", Color.BLACK, 34.0)


        contentPane.children.add(text)


        outLink.translateX = 120.0
        outLink.translateY = 30.0

        children.add(outLink)

        outPoints.add(outLink)
    }
}