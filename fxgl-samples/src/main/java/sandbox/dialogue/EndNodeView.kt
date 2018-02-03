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

        val inLink = InLinkPoint()
        addInPoint(inLink)

        val text = FXGL.getUIFactory().newText("END", Color.WHITE, 24.0)


        //children.add(text)


    }
}