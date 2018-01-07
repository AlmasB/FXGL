/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import com.almasb.fxgl.app.FXGL
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ChoiceNodeView : NodeView(220.0, 180.0) {

    init {
        addInPoint(InLinkPoint())


        for (i in 0..3) {

            val field = TextField()
            field.promptText = "Choice $i"

            val outPoint = OutLinkPoint()
            outPoint.translateXProperty().bind(widthProperty().add(-25.0))
            outPoint.translateY = 15 + i * 35.0


            outPoints.add(outPoint)


            addContent(field)

            children.add(outPoint)
        }
    }
}