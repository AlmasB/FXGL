/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.DialogueNode
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import com.almasb.fxgl.dsl.getbp
import com.almasb.fxgl.tools.dialogues.DialogueEditorVars.IS_SHOW_AUDIO_LINES
import com.almasb.fxgl.tools.dialogues.ui.ExpandableTextArea
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.effect.DropShadow
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.FileChooser
import java.io.File

/**
 * A generic view of a dialogue node.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class NodeView(val node: DialogueNode) : Pane() {

    companion object {
        private const val INITIAL_WIDTH = 320.0
        private const val INITIAL_HEIGHT = 100.0

        val colors = mapOf(
                START to SimpleObjectProperty(Color.DARKGREEN),
                END to SimpleObjectProperty(Color.RED),
                FUNCTION to SimpleObjectProperty(Color.BLUE),
                CHOICE to SimpleObjectProperty(Color.GOLD),
                TEXT to SimpleObjectProperty(Color.BEIGE),
                BRANCH to SimpleObjectProperty(Color.MEDIUMVIOLETRED)
        )
    }

    var outPoints = FXCollections.observableArrayList<OutLinkPoint>()

    /**
     * Only start node does not have an in point.
     */
    var inPoint: InLinkPoint? = null

    protected val contentRoot = VBox(10.0)

    protected val textArea = ExpandableTextArea(INITIAL_WIDTH - 70, INITIAL_HEIGHT - 50)

    val closeButton = CustomButton("\u2715", 16.0)

    init {
        styleClass.add("dialogue-editor-node-view")

        isPickOnBounds = false

        textArea.font = Font.font(14.0)
        textArea.textProperty().bindBidirectional(node.textProperty)

        prefWidthProperty().bind(textArea.prefWidthProperty().add(70))
        prefHeightProperty().bind(textArea.prefHeightProperty().add(50.0))

        addContent(textArea)

        val title = Title(node.type.toString().lowercase().replaceFirstChar { it.uppercaseChar() }, colors[node.type]
                ?: SimpleObjectProperty(Color.WHITE))
        title.prefWidthProperty().bind(prefWidthProperty().subtract(4))
        title.translateX = 2.0
        title.translateY = 2.0

        contentRoot.translateX = 35.0
        contentRoot.translateY = 35.0

        closeButton.translateX = prefWidth - 28.0
        closeButton.translateY = 6.0

        effect = DropShadow(10.0, Color.BLACK)

        children.addAll(contentRoot, title, closeButton)
    }

    fun addContent(node: Node) {
        contentRoot.children.add(node)
    }

    fun addInPoint(linkPoint: InLinkPoint) {
        inPoint = linkPoint

        children.add(linkPoint)

        linkPoint.translateX = 10.0
        linkPoint.translateYProperty().bind(heightProperty().divide(2))
    }

    fun addOutPoint(linkPoint: OutLinkPoint) {
        outPoints.add(linkPoint)

        children.add(linkPoint)

        linkPoint.translateXProperty().bind(widthProperty().add(-25))
        linkPoint.translateYProperty().bind(heightProperty().divide(2))
    }

    fun addAudioField() {
        val audioField = AudioField()
        audioField.visibleProperty().bind(getbp(IS_SHOW_AUDIO_LINES))

        audioField.field.text = node.audioFileNameProperty.value

        node.audioFileNameProperty.bindBidirectional(audioField.field.textProperty())

        contentRoot.children.add(0, audioField)

        prefHeightProperty().bind(textArea.prefHeightProperty().add(85))
    }

    private class Title(name: String, colorProperty: ObjectProperty<Color>) : HBox() {

        init {
            styleClass += "title"

            // if color changes, then re-style
            colorProperty.addListener { _, _, newColor ->
                val c = newColor

                style = "-fx-background-color: rgba(${c.red*255}, ${c.green*255}, ${c.blue*255}, 0.85)"
            }

            val c = colorProperty.value

            style = "-fx-background-color: rgba(${c.red*255}, ${c.green*255}, ${c.blue*255}, 0.85)"

            val text = FXGL.getUIFactoryService().newText(name, Color.WHITE, 16.0)

            alignment = Pos.CENTER_LEFT
            padding = Insets(1.0, 15.0, 1.0, 33.0)

            children += text
        }
    }
}

class CustomButton(symbol: String, size: Double = 24.0) : StackPane() {

    init {
        val bg = Rectangle(20.0, 20.0, null)
        val text = FXGL.getUIFactoryService().newText(symbol, size)

        bg.strokeProperty().bind(
                Bindings.`when`(hoverProperty()).then(Color.WHITE).otherwise(Color.TRANSPARENT)
        )

        cursor = Cursor.HAND

        children.addAll(bg, text)
    }
}

class AudioField() : HBox(5.0) {

    companion object {
        private val audioFileChooser = FileChooser()
    }

    val field = TextField()

    init {
        audioFileChooser.initialDirectory = File(System.getProperty("user.dir"))

        val icon = makeSoundIcon()

        field.prefWidth = 190.0

        val button = CustomButton("...", 14.0)
        button.setOnMouseClicked {

            // TODO: configure as appropriate
            audioFileChooser.showOpenDialog(null)?.let { file ->
                field.text = File.separatorChar + "assets" + file.absolutePath.toString().substringAfter("assets")

                // remember dir
                audioFileChooser.initialDirectory = file.parentFile
            }
        }

        children.addAll(icon, field, button)
    }

    private fun makeSoundIcon(): Node {
        val hbox = HBox(2.0)
        hbox.alignment = Pos.CENTER

        arrayOf(3.0, 5.0, 4.0, 6.0, 8.0, 5.5, 2.0)
                .forEach { h ->
                    val rect = Rectangle(2.0, h * 2.0, Color.WHITE)
                    rect.arcWidth = 1.0
                    rect.arcHeight = 1.5

                    hbox.children += rect
                }

        return hbox
    }
}