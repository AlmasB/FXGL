/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationBuilder
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.input.KeyTrigger
import com.almasb.fxgl.input.Trigger
import com.almasb.fxgl.input.TriggerListener
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.logging.Logger
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
class DialogueScene(private val sceneService: SceneService) : SubScene() {

    private val log = Logger.get<DialogueScene>()

    private val animation1: Animation<*>
    private val animation2: Animation<*>

    private val topText = Text()
    private val boxPlayerLines = VBox(5.0)

    private var onFinished: Runnable = EmptyRunnable

    private lateinit var graph: DialogueGraph
    private lateinit var functionHandler: FunctionCallHandler

    internal lateinit var gameVars: PropertyMap

    init {
        val appWidth = sceneService.appWidth
        val appHeight = sceneService.appHeight

        val topLine = Rectangle(appWidth.toDouble(), 150.0)
        topLine.translateY = -150.0

        val botLine = Rectangle(appWidth.toDouble(), 200.0)
        botLine.translateY = appHeight.toDouble()

        animation1 = AnimationBuilder()
                .duration(Duration.seconds(0.5))
                .translate(topLine)
                .from(Point2D(0.0, -150.0))
                .to(Point2D.ZERO)
                .build()

        animation2 = AnimationBuilder()
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

        boxPlayerLines.translateX = 50.0
        boxPlayerLines.translateY = appHeight.toDouble() - 160.0
        boxPlayerLines.opacity = 0.0

        val keyView = KeyView(KeyCode.ENTER, Color.GREENYELLOW, 18.0)
        keyView.translateX = appWidth.toDouble() - 80.0
        keyView.translateY = appHeight - 40.0

        keyView.opacityProperty().bind(boxPlayerLines.opacityProperty())
        topText.opacityProperty().bind(boxPlayerLines.opacityProperty())

        initUserActions()

        contentRoot.children.addAll(topLine, botLine, topText, boxPlayerLines, keyView)
    }

    private fun initUserActions() {
        val userAction = object : UserAction("Next RPG Line") {
            override fun onActionBegin() {
                if (currentNode.type == DialogueNodeType.CHOICE) {
                    return
                }

                nextLine()
            }
        }

        val digitTriggerListener = object : TriggerListener() {
            override fun onActionBegin(trigger: Trigger) {
                // only allow 1,2,3 select
                if (currentNode.type != DialogueNodeType.CHOICE) {
                    return
                }

                if (trigger is KeyTrigger && trigger.key.toString().startsWith("DIGIT")) {
                    val idString = trigger.key.toString().removePrefix("DIGIT")

                    // careful, these are unsafe operations assuming that properties[] are present
                    val localID: Int? = boxPlayerLines.children
                            .find { it.properties["idString"] == idString }
                            ?.properties
                            ?.get("localID") as? Int

                    localID?.let {
                        selectLine(it)
                    }
                }
            }
        }

        input.addAction(userAction, KeyCode.ENTER)
        input.addTriggerListener(digitTriggerListener)
    }

    override fun onCreate() {
        animation2.onFinished = Runnable {
            onOpen()
        }

        animation1.start()
        animation2.start()
    }

    override fun onUpdate(tpf: Double) {
        animation1.onUpdate(tpf)
        animation2.onUpdate(tpf)

        if (message.isNotEmpty()) {
            topText.text += message.poll()
        }
    }

    private fun endCutscene() {
        boxPlayerLines.opacity = 0.0
        animation2.onFinished = Runnable {
            sceneService.popSubScene()
            onClose()
        }
        animation1.startReverse()
        animation2.startReverse()
    }

    fun start(cutscene: DialogueGraph, functionHandler: FunctionCallHandler, onFinished: Runnable) {
        this.graph = cutscene
        this.functionHandler = functionHandler
        this.onFinished = onFinished

        currentNode = graph.startNode

        nextLine()

        sceneService.pushSubScene(this)
    }

    private var currentLine = 0
    private lateinit var currentNode: DialogueNode
    private val message = ArrayDeque<Char>()

    private var stringID = 1

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

                    stringID = 0

                    choiceNode.conditions.forEach { id, condition ->

                        // TODO: condition might be empty ...
                        if (condition.value.evaluate()) {
                            val option = choiceNode.options[id]!!

                            populatePlayerLine(id, option.value.parseVariables())
                        }
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
        stringID++
        val idString = "$stringID"

        val text = Text("${idString}. ${data}")
        text.font = Font.font(18.0)
        text.fillProperty().bind(
                Bindings.`when`(text.hoverProperty())
                        .then(Color.YELLOW)
                        .otherwise(Color.WHITE)
        )
        text.properties["idString"] = idString
        text.properties["localID"] = localID

        text.setOnMouseClicked {
            selectLine(localID)
        }

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
        onFinished.run()
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
        val funcCalls = this.split("\n".toRegex())

        funcCalls.forEach {
            val tokens = it.trim().split(" +".toRegex())

            if (tokens.isNotEmpty()) {
                val funcName = tokens[0].trim()

                if (funcName.isNotEmpty()) {
                    // TODO: tokens with $... expanded?
                    functionHandler.handle(funcName, tokens.drop(1).map { it.trim() }.toTypedArray())
                }
            }
        }
    }

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