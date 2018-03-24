/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import com.almasb.fxgl.app.FXGL
import javafx.scene.control.TextArea
import javafx.scene.paint.Color
import javafx.scene.text.Font

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextNodeView : NodeView(250 + 2*35.0, 120.0) {

    val inLink = InLinkPoint()
    val outLink = OutLinkPoint()

    init {

        addInPoint(inLink)


        val textArea = TextArea()
        textArea.isWrapText = true

        textArea.prefWidth = 250.0
        textArea.prefHeight = prefHeight - 20.0

        textArea.font = Font.font(16.0)


        addContent(textArea)

        addOutPoint(outLink)



//        val icon = WindowIcon()
//        icon.styleClass.setAll("window-add-icon");
//
//        icon.stylesheets.add(FXGL.getAssetLoader().loadCSS("editor.css").externalForm)
//
//        icon.rotate = 45.0
//        rightIcons.add(icon)
    }
}