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



        //children.add(FXGL.getUIFactory().newText("START", Color.WHITE, 24.0))




        addOutPoint(outLink)
    }
}