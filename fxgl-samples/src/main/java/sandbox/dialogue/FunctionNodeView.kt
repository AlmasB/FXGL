/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.asset.FXGLAssets
import javafx.scene.control.TextArea
import javafx.scene.paint.Color

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FunctionNodeView : NodeView() {

    val inLink = InLinkPoint()
    val outLink = OutLinkPoint()

    init {

        val textArea = TextArea()
        textArea.isWrapText = true

        // TODO: color script
        //textArea.textFormatter
        textArea.font = FXGLAssets.UI_MONO_FONT.newFont(16.0)

        contentPane.children.add(textArea)

        setPrefSize(350.0, 200.0)

        inLink.translateX = -15.0
        inLink.translateY = 30.0

        outLink.translateX = 350.0
        outLink.translateY = 30.0

        children.addAll(inLink, outLink)

        outPoints.add(outLink)
        inPoints.add(inLink)
    }
}