/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.ui.FontType
import com.almasb.fxgl.ui.InGameWindow
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ScriptNodeView : NodeView() {

    val inLink = InLinkPoint()
    val outLink = OutLinkPoint()

    init {

        addInPoint(inLink)


        val field = TextField()
        field.promptText = "Script file name"


        val btn = Button("Embed script")
        btn.setOnAction {
            val w = InGameWindow("Embed script")

            val textArea = TextArea()
            textArea.isWrapText = true

            // TODO: color script
            //textArea.textFormatter
            textArea.font = FXGL.getUIFactory().newFont(FontType.MONO, 16.0)

            textArea.text = ""

            //w.contentPane.children.add(textArea)

            w.setPrefSize(600.0, 300.0)
            w.relocate(300.0, 300.0)

            FXGL.getApp().gameScene.addUINode(w)

        }

        prefWidth = 200.0


        addContent(field)
        addContent(btn)

        //vbox.children.addAll(field, btn)

        //children.addAll(vbox)

        addOutPoint(outLink)


    }
}