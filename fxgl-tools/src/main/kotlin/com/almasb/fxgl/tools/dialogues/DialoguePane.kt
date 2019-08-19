/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.cutscene.dialogue.*
import com.almasb.fxgl.dsl.*
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ListChangeListener
import javafx.collections.MapChangeListener
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.effect.Glow
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import javafx.stage.FileChooser
import javafx.util.Duration
import java.io.File
import java.nio.file.Files

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialoguePane : Pane() {

    companion object {
        val highContrastProperty = SimpleBooleanProperty(false)

        private val branch: (DialogueNode) -> NodeView = { BranchNodeView(it) }
        private val end: (DialogueNode) -> NodeView = { EndNodeView(it) }
        private val start: (DialogueNode) -> NodeView = { StartNodeView(it) }
        private val function: (DialogueNode) -> NodeView = { FunctionNodeView(it) }
        private val text: (DialogueNode) -> NodeView = { TextNodeView(it) }
        private val choice: (DialogueNode) -> NodeView = { ChoiceNodeView(it) }

        val nodeConstructors = mapOf<DialogueNodeType, () -> DialogueNode>(
                DialogueNodeType.BRANCH to { BranchNode("") },
                DialogueNodeType.END to { EndNode("") },
                DialogueNodeType.START to { StartNode("") },
                DialogueNodeType.FUNCTION to { FunctionNode("") },
                DialogueNodeType.TEXT to { TextNode("") },
                DialogueNodeType.CHOICE to { ChoiceNode("") }
        )

        val nodeViewConstructors = mapOf<DialogueNodeType, (DialogueNode) -> NodeView>(
                DialogueNodeType.BRANCH to branch,
                DialogueNodeType.END to end,
                DialogueNodeType.START to start,
                DialogueNodeType.FUNCTION to function,
                DialogueNodeType.TEXT to text,
                DialogueNodeType.CHOICE to choice
        )
    }

    private val contentRoot = Group()

    private var selectedNodeView: NodeView? = null
    private var selectedOutLink: OutLinkPoint? = null

    private var graph = DialogueGraph()

    private val views = Group()
    private val edgeViews = Group()
    private val nodeViews = Group()

    private val scale = Scale()

    private val dragScale = 1.35
    private var dragX = 0.0
    private var dragY = 0.0

    private var mouseX = 0.0
    private var mouseY = 0.0

    private val mouseGestures = MouseGestures(contentRoot)

    private val toolbar = HBox(15.0)

    init {
        toolbar.setPrefSize(getAppWidth().toDouble(), 30.0)
        toolbar.translateY = -toolbar.prefHeight
        toolbar.style = "-fx-background-color: black"
        toolbar.alignment = Pos.CENTER_LEFT

        val itemSave = MenuItem("Save")
        itemSave.setOnAction {
            openSaveDialog()
        }

        val menuFile = Menu("")
        menuFile.graphic = Text("File").also { it.fill = Color.WHITE }
        menuFile.style = "-fx-background-color: black"
        menuFile.items.addAll(itemSave, MenuItem("Load").also { it.setOnAction { openLoadDialog() } })

        val menuBar = MenuBar()
        menuBar.style = "-fx-background-color: black"
        menuBar.menus.addAll(menuFile)

        toolbar.children += menuBar


        translateY = toolbar.prefHeight
        setPrefSize(getAppWidth().toDouble(), getAppHeight().toDouble())
        style = "-fx-background-color: gray"


        toolbar.children += makeRunButton()

        toolbar.children += getUIFactory().newText("High contrast")

        toolbar.children += CheckBox().also {
            it.selectedProperty().bindBidirectional(highContrastProperty)
        }

        contentRoot.children.addAll(
                edgeViews, views, nodeViews
        )


        val translate = Translate()

        contentRoot.transforms += scale
        contentRoot.transforms += translate

        setOnScroll {
            val scaleFactor = if (it.deltaY < 0) 0.95 else 1.05

            scale.x *= scaleFactor
            scale.y *= scaleFactor
        }

        children.addAll(contentRoot, toolbar)

        // start and end

        createNode(StartNodeView(), 50.0, getAppHeight() / 2.0)
        createNode(EndNodeView(), getAppWidth() - 370.0, getAppHeight() / 2.0)

        initContextMenu()

        setOnMouseMoved {
            dragX = it.x
            dragY = it.y

            mouseX = it.sceneX
            mouseY = it.sceneY
        }

        setOnMouseDragged {
            if (mouseGestures.isDragging || it.button != MouseButton.PRIMARY)
                return@setOnMouseDragged

            contentRoot.translateX += (it.x - dragX) * dragScale
            contentRoot.translateY += (it.y - dragY) * dragScale

            dragX = it.x
            dragY = it.y
        }

        initGraphListeners()
    }

    private fun initContextMenu() {
        val contextMenu = ContextMenu()
        contextMenu.items.addAll(
                DialogueNodeType.values().map { type ->
                    MenuItem(type.toString()).also {
                        it.setOnAction {
                            val nodeCtor = nodeConstructors[type] ?: throw IllegalArgumentException("No constructor found for type: $type")
                            graph.addNode(nodeCtor())
                        }
                    }
                }
        )

        setOnContextMenuRequested {
            if (it.target !== this)
                return@setOnContextMenuRequested

            contextMenu.show(contentRoot.scene.window, it.sceneX + 150.0, it.sceneY + 45.0)
        }
    }

    private fun initGraphListeners() {
        graph.nodes.addListener { c: MapChangeListener.Change<out Int, out DialogueNode> ->
            if (c.wasAdded()) {
                val node = c.valueAdded

                onAdded(node)

            } else if (c.wasRemoved()) {
                val node = c.valueRemoved

                onRemoved(node)
            }
        }

        graph.edges.addListener { c: ListChangeListener.Change<out DialogueEdge> ->
            while (c.next()) {
                if (c.wasAdded()) {
                    c.addedSubList.forEach { onAdded(it) }
                } else if (c.wasRemoved()) {
                    c.removed.forEach { onRemoved(it) }
                }
            }
        }
    }

    private fun onAdded(node: DialogueNode) {
        val nodeViewConstructor = nodeViewConstructors[node.type] ?: throw IllegalArgumentException("View constructor for ${node.type} does not exist")
        val nodeView = nodeViewConstructor(node)

        val p = contentRoot.sceneToLocal(mouseX, mouseY)

        addNodeView(nodeView, p.x, p.y)
    }

    private fun onRemoved(node: DialogueNode) {
        val nodeView = nodeViews.children
                .map { it as NodeView }
                .find { it.node === node } ?: throw IllegalArgumentException("No view found for node $node")

        // so that user does not accidentally press it again
        nodeView.closeButton.isVisible = false

        animationBuilder()
                .duration(Duration.seconds(0.56))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(Runnable { nodeViews.children -= nodeView })
                .scale(nodeView)
                .from(Point2D(1.0, 1.0))
                .to(Point2D.ZERO)
                .buildAndPlay()
    }

    private fun onAdded(edge: DialogueEdge) {

    }

    private fun onRemoved(edge: DialogueEdge) {
        val edgeView = edgeViews.children
                .map { it as EdgeView }
                .find { it.source.owner.node === edge.source && it.target.owner.node === edge.target }
                ?: throw IllegalArgumentException("No edge view found for edge $edge")

        val p1 = Point2D(edgeView.startX, edgeView.startY)
        val p2 = Point2D(edgeView.controlX1, edgeView.controlY1)
        val p3 = Point2D(edgeView.controlX2, edgeView.controlY2)
        val p4 = Point2D(edgeView.endX, edgeView.endY)

        val group = Group()
        group.effect = Glow(0.7)

        val numSegments = 350

        for (t in 0..numSegments) {
            val delay = if (graph.findNodeID(edgeView.source.owner.node) == -1) t else (numSegments - t)

            val p = FXGLMath.bezier(p1, p2, p3, p4, t / numSegments.toDouble())

            val c = Circle(p.x, p.y, 2.0, edgeView.stroke)

            group.children += c

            animationBuilder()
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .delay(Duration.millis(delay * 2.0))
                    .duration(Duration.seconds(0.35))
                    .fadeOut(c)
                    .buildAndPlay()
        }

        views.children += group

        runOnce({ views.children -= group }, Duration.seconds(7.0))

        edgeView.source.disconnect()

        edgeViews.children -= edgeView
    }

    private fun makeRunButton(): Node {
        val stack = StackPane()

        val bgRun = Rectangle(18.0, 18.0, null)
        bgRun.strokeProperty().bind(
                Bindings.`when`(stack.hoverProperty()).then(Color.WHITE).otherwise(Color.TRANSPARENT)
        )
        bgRun.fillProperty().bind(
                Bindings.`when`(stack.pressedProperty()).then(Color.color(0.5, 0.5, 0.5, 0.95)).otherwise(Color.TRANSPARENT)
        )

        val btnRun = Polygon(
                0.0, 0.0,
                13.0, 6.5,
                0.0, 13.0
        )
        btnRun.fill = Color.LIGHTGREEN

        stack.setOnMouseClicked {
            stack.requestFocus()
            DialogueScene(getGameController(), getAppWidth(), getAppHeight()).start(graph)
        }

        stack.children.addAll(bgRun, btnRun)

        return stack
    }

    private fun createNode(nodeView: NodeView, x: Double, y: Double) {
        graph.addNode(nodeView.node)

        addNodeView(nodeView, x, y)
    }

    private fun addNodeView(nodeView: NodeView, x: Double, y: Double) {
        nodeView.relocate(x, y)

        attachMouseHandler(nodeView)

        nodeViews.children.add(nodeView)
    }

    private fun attachMouseHandler(nodeView: NodeView) {
        mouseGestures.makeDraggable(nodeView)

        nodeView.closeButton.setOnMouseClicked {
            graph.removeNode(nodeView.node)
        }

        nodeView.outPoints.forEach { outPoint ->
            // TODO: refactor repetition
            outPoint.setOnMouseClicked {
                if (it.button == MouseButton.PRIMARY) {
                    selectedOutLink = outPoint
                    selectedNodeView = nodeView
                } else {
                    if (outPoint.isConnected) {
                        disconnectOutLink(outPoint)
                    }
                }
            }
        }

        nodeView.outPoints.addListener { c: ListChangeListener.Change<out OutLinkPoint> ->
            while (c.next()) {
                c.addedSubList.forEach { outPoint ->
                    outPoint.setOnMouseClicked {
                        if (it.button == MouseButton.PRIMARY) {
                            selectedOutLink = outPoint
                            selectedNodeView = nodeView
                        } else {
                            if (outPoint.isConnected) {
                                disconnectOutLink(outPoint)
                            }
                        }
                    }
                }
            }
        }

        nodeView.inPoint?.let { inPoint ->

            inPoint.setOnMouseClicked {

                if (it.button == MouseButton.PRIMARY) {

                    selectedOutLink?.let { outPoint ->

                        if (outPoint.isConnected) {
                            disconnectOutLink(outPoint)
                        }

                        val edgeView = outPoint.connect(inPoint)

                        if (outPoint.choiceLocalID != -1) {
                            edgeView.localID = outPoint.choiceLocalID
                            graph.addEdge(selectedNodeView!!.node, outPoint.choiceLocalID, nodeView.node)
                        } else {
                            graph.addEdge(selectedNodeView!!.node, nodeView.node)
                        }

                        edgeViews.children.add(edgeView)

                        // reset selection
                        selectedOutLink = null
                        selectedNodeView = null
                    }
                }
            }
        }
    }

    private fun disconnectOutLink(outPoint: OutLinkPoint) {
        outPoint.other?.let { inPoint ->
            if (outPoint.choiceLocalID != -1) {
                graph.removeEdge(outPoint.owner.node, outPoint.choiceLocalID, inPoint.owner.node)
            } else {
                graph.removeEdge(outPoint.owner.node, inPoint.owner.node)
            }
        }
    }








    private val mapper = jacksonObjectMapper()

    private fun openSaveDialog() {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.initialFileName = "dialogue_graph.json"

        chooser.showSaveDialog(scene.window)?.let {
            mapper.enable(SerializationFeature.INDENT_OUTPUT)

            val serializedGraph = DialogueGraphSerializer.toSerializable(graph)

            nodeViews.children.map { it as NodeView }.forEach {
                serializedGraph.uiMetadata[graph.findNodeID(it.node)] = SerializablePoint2D(it.layoutX, it.layoutY)
            }

            val s = mapper.writeValueAsString(serializedGraph)

            Files.writeString(it.toPath(), s)
        }
    }

    private fun openLoadDialog() {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.initialFileName = "dialogue_graph.json"

        chooser.showOpenDialog(scene.window)?.let {
            load(mapper.readValue(it, SerializableGraph::class.java))
        }
    }

    private fun load(serializedGraph: SerializableGraph) {
//        graph = DialogueGraphSerializer.fromSerializable(serializedGraph)
//
//        nodeViews.children.clear()
//        edgeViews.children.clear()
//
//        graph.nodes.forEach { (id, node) ->
//            val x = serializedGraph.uiMetadata[id]?.x ?: 100.0
//            val y = serializedGraph.uiMetadata[id]?.y ?: 100.0
//
//            when (node.type) {
//                DialogueNodeType.START -> {
//                    addNodeView(StartNodeView(node), x, y)
//                }
//
//                DialogueNodeType.END -> {
//                    addNodeView(EndNodeView(node), x, y)
//                }
//
//                DialogueNodeType.TEXT -> {
//                    addNodeView(TextNodeView(node), x, y)
//                }
//
//                DialogueNodeType.CHOICE -> {
//                    addNodeView(ChoiceNodeView(node), x, y)
//                }
//
//                DialogueNodeType.FUNCTION -> {
//                    addNodeView(FunctionNodeView(node), x, y)
//                }
//                DialogueNodeType.BRANCH -> {
//                    addNodeView(BranchNodeView(node), x, y)
//                }
//
//                else -> throw IllegalArgumentException("Unknown node type: ${node.type}")
//            }
//        }
//
//        graph.edges.forEach { edge ->
//            val source = nodeViews.children.map { it as NodeView }.find { it.node === edge.source }
//            val target = nodeViews.children.map { it as NodeView }.find { it.node === edge.target }
//
//            if (source != null && target != null) {
//                val edgeView = source.outPoints[0].connect(target.inPoint!!)
//
//                edgeViews.children.add(edgeView)
//            }
//        }
//
//        graph.choiceEdges.forEach { edge ->
//            val source = nodeViews.children.map { it as NodeView }.find { it.node === edge.source }
//            val target = nodeViews.children.map { it as NodeView }.find { it.node === edge.target }
//
//            if (source != null && target != null) {
//                source.outPoints.find { it.choiceLocalID == edge.optionID }?.let { outPoint ->
//                    val edgeView = outPoint.connect(target.inPoint!!)
//
//                    edgeViews.children.add(edgeView)
//                }
//            }
//        }
    }
}