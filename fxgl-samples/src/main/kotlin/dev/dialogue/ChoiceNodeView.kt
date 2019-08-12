/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import javafx.scene.control.TextField

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ChoiceNodeView : NodeView(ChoiceNode("")) {

    init {
        setPrefSize(220.0, 180.0)

        addInPoint(InLinkPoint())

        val node = this.node as ChoiceNode

        for (i in 0..3) {

            val field = TextField()
            field.promptText = "Choice $i"

            val outPoint = OutLinkPoint()
            outPoint.translateXProperty().bind(widthProperty().add(-25.0))
            outPoint.translateY = 40 + i * 35.0


            outPoint.choiceLocalID = i
            outPoint.choiceLocalOptionProperty.bind(field.textProperty())

            node.localIDs += i


            outPoints.add(outPoint)


            addContent(field)

            children.add(outPoint)
        }
    }
}