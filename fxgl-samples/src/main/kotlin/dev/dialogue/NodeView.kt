/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.runOnce
import javafx.beans.binding.Bindings
import javafx.scene.Node
import javafx.scene.control.TextArea
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import sandbox.cutscene.MouseGestures
import java.util.concurrent.Callable
import javafx.scene.control.ScrollBar
import javafx.util.Duration
import kotlin.math.abs


/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class NodeView(val node: DialogueNode) : Pane() {

    var w: Double = 250.0 + 70.0
    var h: Double = 100.0

    var outPoints = arrayListOf<OutLinkPoint>()

    var inPoints = arrayListOf<InLinkPoint>()

    private val mouseGestures = MouseGestures()

    val contentRoot = VBox(10.0)

    val textArea = TextArea()

    val text = Text()

    var prevHeight = 0.0

    init {
        setPrefSize(w, h)

        //background = Background(BackgroundFill(Color.color(0.25, 0.25, 0.25, 0.75), null, null))

        styleClass.add("dialogue-editor-node-view")

        mouseGestures.makeDraggable(this)

        contentRoot.translateX = 35.0
        contentRoot.translateY = 10.0



        val title = FXGL.getUIFactory().newText(node.type.toString().toLowerCase().capitalize(), Color.WHITE, 16.0)


        children.addAll(contentRoot)


        textArea.styleClass.add("dialogue-editor-text-area")
        textArea.isWrapText = true
        textArea.prefWidth = 250.0
        textArea.prefHeight = prefHeight - 50.0

        prevHeight = textArea.prefHeight

        text.translateY = 450.0
        text.font = Font.font(14.0)
        text.opacity = 0.0
        text.textProperty().bind(textArea.textProperty())
        text.wrappingWidth = textArea.prefWidth - 30.0

        textArea.prefHeightProperty().bind(
                Bindings.createDoubleBinding(Callable {

                    var newHeight = text.layoutBounds.height + 20.0
                    newHeight = if (abs(prevHeight - newHeight) > 15) newHeight else prevHeight

                    prevHeight = newHeight

                    return@Callable newHeight
                }, text.layoutBoundsProperty())
        )

        prefHeightProperty().bind(textArea.prefHeightProperty().add(50.0))

        textArea.font = Font.font(14.0)
        textArea.textProperty().bindBidirectional(node.textProperty)

        addContent(title)
        addContent(textArea)

//        runOnce({
//            val bar = textArea.lookup(".scroll-bar:vertical") as ScrollBar
//            bar.isDisable = true
//            bar.opacity = 0.0
//        }, Duration.seconds(3.0))
    }

    fun addContent(node: Node) {
        contentRoot.children.add(node)
    }

    fun addInPoint(linkPoint: InLinkPoint) {
        inPoints.add(linkPoint)

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
}