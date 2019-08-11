/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import javafx.scene.control.TextArea
import javafx.scene.text.Font

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextNodeView : NodeView(TextNode("")) {

    val inLink = InLinkPoint()
    val outLink = OutLinkPoint()

    init {
        setPrefSize(250 + 2*35.0, 220.0)

        val textArea = TextArea()
        textArea.isWrapText = true

        textArea.prefWidth = 250.0
        textArea.prefHeight = prefHeight - 50.0

        textArea.font = Font.font(16.0)
        textArea.textProperty().bindBidirectional(node.textProperty)


        addContent(textArea)

        addInPoint(inLink)
        addOutPoint(outLink)
    }
}