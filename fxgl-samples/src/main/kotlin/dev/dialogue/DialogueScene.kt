/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationDSL
import com.almasb.fxgl.cutscene.CutsceneDialogLine
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.scene.SubSceneStack
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialogueScene(private val sceneStack: SubSceneStack, appWidth: Int, appHeight: Int) : SubScene() {

    private val animation: Animation<*>
    private val animation2: Animation<*>

    private val topText = Text()
    private val botText = VBox(5.0)

    private lateinit var graph: DialogueGraph

    init {
        val topLine = Rectangle(appWidth.toDouble(), 150.0)
        topLine.translateY = -150.0

        val botLine = Rectangle(appWidth.toDouble(), 200.0)
        botLine.translateY = appHeight.toDouble()

        animation = AnimationDSL()
                .duration(Duration.seconds(0.5))
                .translate(topLine)
                .from(Point2D(0.0, -150.0))
                .to(Point2D.ZERO)
                .build()

        animation2 = AnimationDSL()
                .duration(Duration.seconds(0.5))
                .translate(botLine)
                .from(Point2D(0.0, appHeight.toDouble()))
                .to(Point2D(0.0, appHeight.toDouble() - 200.0))
                .build()

        topText.fill = Color.WHITE
        topText.font = Font.font(18.0)
        topText.wrappingWidth = appWidth.toDouble() - 155.0
        topText.translateX = 50.0
        topText.translateY = 40.0
        topText.opacity = 0.0

//        botText.fill = Color.WHITE
//        botText.font = Font.font(18.0)
//        botText.wrappingWidth = appWidth.toDouble() - 155.0
        botText.translateX = 50.0
        botText.translateY = appHeight.toDouble() - 160.0
        botText.opacity = 0.0

        val keyView = KeyView(KeyCode.ENTER, Color.GREENYELLOW, 18.0)
        keyView.translateX = appWidth.toDouble() - 80.0
        keyView.translateY = appHeight - 40.0

        keyView.opacityProperty().bind(botText.opacityProperty())
        topText.opacityProperty().bind(botText.opacityProperty())

        contentRoot.children.addAll(topLine, botLine, topText, botText, keyView)

        input.addAction(object : UserAction("Next RPG Line") {
            override fun onActionBegin() {
                nextLine()
            }
        }, KeyCode.ENTER)
    }

    private fun centerTextBind(text: Text, x: Double, y: Double) {
        text.layoutBoundsProperty().addListener { _, _, bounds ->
            text.translateX = x - bounds.width / 2
            text.translateY = y - bounds.height / 2
        }
    }

    override fun onCreate() {
        animation2.onFinished = Runnable {
            onOpen()
        }

        animation.start()
        animation2.start()
    }

    override fun onUpdate(tpf: Double) {
        animation.onUpdate(tpf)
        animation2.onUpdate(tpf)

        if (message.isNotEmpty()) {
            topText.text += message.poll()
        }
    }

    private fun endCutscene() {
        botText.opacity = 0.0
        animation2.onFinished = Runnable {
            sceneStack.popSubScene()
            onClose()
        }
        animation.startReverse()
        animation2.startReverse()
    }

    fun start(cutscene: DialogueGraph) {
        this.graph = cutscene

        currentNode = graph.nodes.find { it.id == 0}!!

        nextLine()

        sceneStack.pushSubScene(this)
    }

    private var currentLine = 0
    private lateinit var currentNode: DialogueNode
    private val message = ArrayDeque<Char>()

    private fun nextLine() {
        // do not allow to move to next line while the text animation is going
        if (message.isNotEmpty())
            return


        if (currentLine == 0 && currentNode.type == DialogueNodeType.START) {
            currentNode.text.forEach { message.addLast(it) }
            currentLine++
            return
        }



        val isDone = currentNode.type == DialogueNodeType.END
        if (!isDone) {
            currentNode = nextNode()

            currentNode.text.forEach { message.addLast(it) }

            topText.text = ""

        } else {
            endCutscene()
        }
    }

    private fun nextNode(): DialogueNode {
        return graph.edges.find { it.source.id == currentNode.id }!!.target
    }

    private fun nextNodeFromChoice(choiceID: Int): DialogueNode {
        return graph.choiceEdges.find { it.source.id == currentNode.id && it.localID == choiceID }!!.target
    }

    private fun onOpen() {
        botText.opacity = 1.0
    }

    private fun onClose() {
        currentLine = 0
        message.clear()
    }
}