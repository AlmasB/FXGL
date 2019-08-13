/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.text.Font

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextNodeView : NodeView(TextNode("")) {

    val inLink = InLinkPoint(this)
    val outLink = OutLinkPoint(this)

    init {
        addInPoint(inLink)
        addOutPoint(outLink)
    }
}

class StartNodeView : NodeView(StartNode("")) {

    val outLink = OutLinkPoint(this)

    init {
        addOutPoint(outLink)
    }
}

class EndNodeView : NodeView(EndNode("")) {

    init {
        val inLink = InLinkPoint(this)
        addInPoint(inLink)
    }
}

class ChoiceNodeView : NodeView(ChoiceNode("")) {

    init {
        addInPoint(InLinkPoint(this))

        val node = this.node as ChoiceNode

        for (i in 0..1) {

            val field = TextField()
            field.promptText = "Choice $i"

            val outPoint = OutLinkPoint(this)
            outPoint.translateXProperty().bind(widthProperty().add(-25.0))
            outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(53 + i * 35.0))

            outPoint.choiceLocalID = i
            outPoint.choiceLocalOptionProperty.bind(field.textProperty())

            node.localIDs += i


            outPoints.add(outPoint)


            addContent(field)

            children.add(outPoint)
        }

        prefHeightProperty().bind(children[children.size - 1].translateYProperty().add(35.0))
    }
}