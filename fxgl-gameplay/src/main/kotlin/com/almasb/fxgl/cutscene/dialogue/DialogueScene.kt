/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationBuilder
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.audio.Music
import com.almasb.fxgl.core.asset.AssetLoaderService
import com.almasb.fxgl.core.asset.AssetType
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import com.almasb.fxgl.input.KeyTrigger
import com.almasb.fxgl.input.Trigger
import com.almasb.fxgl.input.TriggerListener
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.Group
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
    internal lateinit var assetLoader: AssetLoaderService
    internal lateinit var audioPlayer: AudioPlayer

    private lateinit var dialogueScriptRunner: DialogueScriptRunner

    init {
        val topLine = Rectangle(0.0, 150.0)
        topLine.widthProperty().bind(sceneService.prefWidthProperty())
        topLine.translateY = -150.0

        val botLine = Rectangle(0.0, 200.0)
        botLine.widthProperty().bind(sceneService.prefWidthProperty())
        botLine.translateYProperty().bind(sceneService.prefHeightProperty())

        val botLineGroup = Group(botLine)

        animation1 = AnimationBuilder()
                .duration(Duration.seconds(0.5))
                .translate(topLine)
                .from(Point2D(0.0, -150.0))
                .to(Point2D.ZERO)
                .build()

        animation2 = AnimationBuilder()
                .duration(Duration.seconds(0.5))
                .translate(botLineGroup)
                .from(Point2D(0.0, 0.0))
                .to(Point2D(0.0, -200.0))
                .build()
        
        topText.fill = Color.WHITE
        topText.font = Font.font(18.0)
        topText.wrappingWidthProperty().bind(sceneService.prefWidthProperty().subtract(155))
        topText.translateX = 50.0
        topText.translateY = 40.0

        boxPlayerLines.translateX = 50.0
        boxPlayerLines.translateYProperty().bind(sceneService.prefHeightProperty().subtract(160))
        boxPlayerLines.opacity = 0.0

        val keyView = KeyView(KeyCode.ENTER, Color.GREENYELLOW, 18.0)
        keyView.translateXProperty().bind(sceneService.prefWidthProperty().subtract(80))
        keyView.translateYProperty().bind(sceneService.prefHeightProperty().subtract(40))

        keyView.opacityProperty().bind(boxPlayerLines.opacityProperty())
        topText.opacityProperty().bind(boxPlayerLines.opacityProperty())

        initUserActions()

        contentRoot.children.addAll(topLine, botLineGroup, topText, boxPlayerLines, keyView)
    }

    private fun initUserActions() {
        val userAction = object : UserAction("Next RPG Line") {
            override fun onActionBegin() {
                if (currentNode.type == CHOICE) {
                    return
                }

                nextLine()
            }
        }

        val digitTriggerListener = object : TriggerListener() {
            override fun onActionBegin(trigger: Trigger) {

                // ignore any presses if type is not CHOICE or the text animation is still going
                if (currentNode.type != CHOICE || message.isNotEmpty()) {
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

    fun start(dialogueGraph: DialogueGraph, context: DialogueContext, functionHandler: FunctionCallHandler, onFinished: Runnable) {
        graph = dialogueGraph.copy()
        this.functionHandler = functionHandler
        this.onFinished = onFinished
        dialogueScriptRunner = DialogueScriptRunner(gameVars, context.properties(), functionHandler)

        // while graph has subdialogue nodes, expand
        while (graph.nodes.any { it.value.type == SUBDIALOGUE }) {
            val subDialogueNode = graph.nodes.values.find { it.type == SUBDIALOGUE }!!

            val subGraph = loadSubDialogue(subDialogueNode as SubDialogueNode)

            val source = graph.edges.find { it.target === subDialogueNode }!!.source
            val target = graph.nextNode(subDialogueNode)!!

            // break the chain source -> subdialogue -> target
            graph.removeNode(subDialogueNode)

            // connect the source and target with a graph
            graph.appendGraph(source, target, subGraph)
        }

        currentNode = graph.startNode

        nextLine()

        sceneService.pushSubScene(this)
    }

    private fun loadSubDialogue(subDialogueNode: SubDialogueNode): DialogueGraph {
        return assetLoader.load(AssetType.DIALOGUE, subDialogueNode.text)
    }

    private var currentLine = 0
    private lateinit var currentNode: DialogueNode

    /**
     * This is the text that is shown to the player one char at a time.
     */
    private val message = ArrayDeque<Char>()

    private var stringID = 1

    private fun nextLine(nextNode: DialogueNode? = null) {
        // do not allow to move to next line while the text animation is going
        if (message.isNotEmpty()) {
            // just show the text fully
            while (message.isNotEmpty()) {
                topText.text += message.poll()
            }
            return
        }

        if (currentLine == 0 && currentNode.type == START) {
            dialogueScriptRunner.replaceVariablesInText(currentNode.text).forEach { message.addLast(it) }
            currentLine++
            return
        }

        val isDone = currentNode.type == END
        if (!isDone) {
            currentNode = nextNode ?: nextNode()
            playAudioLines(currentNode)

            if (currentNode.type == FUNCTION) {
                currentNode.text.parseAndCallFunctions()

                nextLine(nextNode())
            } else if (currentNode.type == BRANCH) {
                val branchNode = currentNode as BranchNode

                val choiceLocalID: Int

                if (branchNode.text.trim().isNotEmpty()) {
                    val result = dialogueScriptRunner.callBooleanFunction(branchNode.text)

                    choiceLocalID = if (result) 0 else 1
                } else {
                    log.warning("Branch node has no function call: ${branchNode.text}. Assuming <false> branch.")
                    choiceLocalID = 1
                }

                nextLine(nextNodeFromChoice(choiceLocalID))
            } else {
                dialogueScriptRunner.replaceVariablesInText(currentNode.text).forEach { message.addLast(it) }

                if (currentNode.type == CHOICE) {
                    val choiceNode = currentNode as ChoiceNode

                    stringID = 0

                    choiceNode.conditions.forEach { id, condition ->

                        if (condition.value.trim().isEmpty() || dialogueScriptRunner.callBooleanFunction(condition.value)) {
                            val option = choiceNode.options[id]!!

                            populatePlayerLine(id, dialogueScriptRunner.replaceVariablesInText(option.value))
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

    private fun playAudioLines(node: DialogueNode) {
        if (node.audioFileName.isEmpty())
            return

        // TODO: store audio being played, so we can stop as appropriate
        val audio = assetLoader.load<Music>(AssetType.MUSIC, assetLoader.getURL(node.audioFileName.replace("\\", "/")))

        audioPlayer.stopMusic(audio)
        audioPlayer.playMusic(audio)
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
            // do not allow to move to next line while the text animation is going
            if (message.isNotEmpty()) {
                // just show the text fully
                while (message.isNotEmpty()) {
                    topText.text += message.poll()
                }

                return@setOnMouseClicked
            }

            selectLine(localID)
        }

        boxPlayerLines.children.add(text)
    }

    private fun selectLine(choiceLocalID: Int) {
        boxPlayerLines.children.clear()
        nextLine(nextNodeFromChoice(choiceLocalID))
    }

    private fun nextNode(): DialogueNode {
        return graph.nextNode(currentNode) ?: makeDummyEndNode("No next node from $currentNode")
    }

    private fun nextNodeFromChoice(optionID: Int): DialogueNode {
        return graph.nextNode(currentNode, optionID) ?: makeDummyEndNode("No next node from $currentNode using option $optionID")
    }

    private fun makeDummyEndNode(text: String): DialogueNode {
        log.warning(text)
        return EndNode(text)
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

    private fun String.parseAndCallFunctions() {
        val funcCalls = this.split("\n".toRegex())

        funcCalls.forEach { line ->
            if (line.trim().isNotEmpty()) {
                dialogueScriptRunner.callFunction(line)
            }
        }
    }
}

