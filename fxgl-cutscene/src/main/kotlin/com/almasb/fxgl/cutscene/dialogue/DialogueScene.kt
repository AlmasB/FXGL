/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationDSL
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.scene.SubSceneStack
import com.almasb.sslogger.Logger
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
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

    private val log = Logger.get<DialogueScene>()

    private val animation: Animation<*>
    private val animation2: Animation<*>

    private val topText = Text()
    private val boxPlayerLines = VBox(5.0)

    private lateinit var graph: DialogueGraph

    internal lateinit var gameVars: PropertyMap

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
        boxPlayerLines.translateX = 50.0
        boxPlayerLines.translateY = appHeight.toDouble() - 160.0
        boxPlayerLines.opacity = 0.0

        val keyView = KeyView(KeyCode.ENTER, Color.GREENYELLOW, 18.0)
        keyView.translateX = appWidth.toDouble() - 80.0
        keyView.translateY = appHeight - 40.0

        keyView.opacityProperty().bind(boxPlayerLines.opacityProperty())
        topText.opacityProperty().bind(boxPlayerLines.opacityProperty())

        contentRoot.children.addAll(topLine, botLine, topText, boxPlayerLines, keyView)

        input.addAction(object : UserAction("Next RPG Line") {
            override fun onActionBegin() {
                if (currentNode.type == DialogueNodeType.CHOICE) {
                    return
                }

                nextLine()
            }
        }, KeyCode.ENTER)
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
        boxPlayerLines.opacity = 0.0
        animation2.onFinished = Runnable {
            sceneStack.popSubScene()
            onClose()
        }
        animation.startReverse()
        animation2.startReverse()
    }

    fun start(cutscene: DialogueGraph) {
        this.graph = cutscene

        currentNode = graph.startNode

        nextLine()

        sceneStack.pushSubScene(this)
    }

    private var currentLine = 0
    private lateinit var currentNode: DialogueNode
    private val message = ArrayDeque<Char>()

    private fun nextLine(nextNode: DialogueNode? = null) {
        // do not allow to move to next line while the text animation is going
        if (message.isNotEmpty())
            return


        if (currentLine == 0 && currentNode.type == DialogueNodeType.START) {
            currentNode.text.parseVariables().forEach { message.addLast(it) }
            currentLine++
            return
        }

        val isDone = currentNode.type == DialogueNodeType.END
        if (!isDone) {
            currentNode = nextNode ?: nextNode()

            if (currentNode.type == DialogueNodeType.FUNCTION) {
                currentNode.text.parseAndExecuteFunctions()

                nextLine(nextNode())
            } else if (currentNode.type == DialogueNodeType.BRANCH) {
                val branchNode = currentNode as BranchNode

                // evaluate branchNode.text
                val choiceLocalID = if (branchNode.text.evaluate()) 0 else 1

                nextLine(nextNodeFromChoice(choiceLocalID))
            } else {
                currentNode.text.parseVariables().forEach { message.addLast(it) }

                if (currentNode.type == DialogueNodeType.CHOICE) {
                    val choiceNode = currentNode as ChoiceNode

                    choiceNode.options.forEach { id, option ->
                        populatePlayerLine(id, option.value.parseVariables())
                    }
                }

                topText.text = ""
            }



        } else {
            topText.text = ""
            endCutscene()
        }
    }

    private fun populatePlayerLine(localID: Int, data: String) {
        val text = Text("${localID + 1}. ${data}")
        text.font = Font.font(18.0)
        text.fillProperty().bind(
                Bindings.`when`(text.hoverProperty())
                        .then(Color.YELLOW)
                        .otherwise(Color.WHITE)
        )

        //text.opacity = 0.0

        text.setOnMouseClicked {
            selectLine(localID)
        }

        // TODO:
//        AnimationDSL()
//                .fadeIn(text)
//                .buildAndPlay(this)

        boxPlayerLines.children.add(text)
    }

    private fun selectLine(choiceLocalID: Int) {
        boxPlayerLines.children.clear()
        nextLine(nextNodeFromChoice(choiceLocalID))
    }

    private fun nextNode(): DialogueNode {
        return graph.nextNode(currentNode) ?: throw IllegalStateException("No next node from $currentNode")
    }

    private fun nextNodeFromChoice(optionID: Int): DialogueNode {
        return graph.nextNode(currentNode, optionID) ?: throw IllegalStateException("No next node from $currentNode using option $optionID")
    }

    private fun onOpen() {
        boxPlayerLines.opacity = 1.0
    }

    private fun onClose() {
        currentLine = 0
        message.clear()
        boxPlayerLines.children.clear()
    }

    private fun String.parseVariables(): String {
        val vars = this.split(" +".toRegex())
                .filter { it.startsWith("\$") && it.length > 1 }
                .map {
                    if (!it.last().isLetterOrDigit())
                        it.substring(1, it.length - 1)
                    else
                        it.substring(1)
                }
                .toSet()

        var result = this

        vars.forEach {
            if (gameVars.exists(it)) {
                val value = gameVars.getValue<Any>(it)
                result = result.replace("\$$it", value.toString())
            }
        }

        return result
    }

    private fun String.parseAndExecuteFunctions() {
        val funcNames = this.split("\n".toRegex())
                .map { it.removeSuffix("()") }

        funcNames.forEach { name ->
            if (gameVars.exists("f_$name")) {
                val func = gameVars.getObject<Runnable>("f_$name")

                func.run()
            } else {
                log.warning("Function $name does not exist in the game variables.")
            }
        }
    }

    // TODO: syntax check
    private fun String.evaluate(): Boolean {
        val tokens = this.split(" +".toRegex())
        val num1 = tokens[0].toInt()
        val num2 = tokens[2].toInt()

        return when (tokens[1]) {

            "<" -> {
                num1 < num2
            }

            ">" -> {
                num1 > num2
            }

            "<=" -> {
                num1 <= num2
            }

            ">=" -> {
                num1 >= num2
            }

            "==" -> {
                num1 == num2
            }

            else -> {
                throw IllegalArgumentException("Parse error")
            }
        }
    }
}