/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.*
import com.almasb.fxgl.dsl.getAppHeight
import com.almasb.fxgl.dsl.getAppWidth
import com.almasb.fxgl.dsl.getGameController
import com.almasb.fxgl.dsl.getUIFactory
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import javafx.stage.FileChooser
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
    }

    private val contentRoot = Group()

    private var selectedNodeView: NodeView? = null
    private var selectedOutLink: OutLinkPoint? = null

    private var graph = DialogueGraph()

    private val edgeViews = Group()
    private val nodeViews = Group()

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
                edgeViews, nodeViews
        )

        val scale = Scale()
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
    }

    private fun initContextMenu() {
        val contextMenu = ContextMenu()
        contextMenu.items.addAll(
                newMenuItem("Text") { TextNodeView() },
                newMenuItem("Choice") { ChoiceNodeView() },
                newMenuItem("Function") { FunctionNodeView() },
                newMenuItem("End") { EndNodeView() },
                newMenuItem("Branch") { BranchNodeView() }
        )

        setOnContextMenuRequested {
            if (it.target !== this)
                return@setOnContextMenuRequested

            contextMenu.show(contentRoot.scene.window, it.sceneX + 150.0, it.sceneY + 45.0)
        }
    }

    private fun newMenuItem(name: String, creator: () -> NodeView) = MenuItem(name).also {
        it.setOnAction {
            val view = creator()
            val p = contentRoot.sceneToLocal(mouseX, mouseY)
            createNode(view, p.x, p.y)
        }
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

        nodeView.inPoints.forEach { inPoint ->

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
        outPoint.disconnect()?.let { inPoint ->
            val view = removeEdgeView(outPoint, inPoint)

            view?.let {
                if (it.localID != -1) {
                    graph.removeEdge(outPoint.owner.node, it.localID, inPoint.owner.node)
                } else {
                    graph.removeEdge(outPoint.owner.node, inPoint.owner.node)
                }
            }
        }
    }

    private fun removeEdgeView(source: OutLinkPoint, target: InLinkPoint): EdgeView? {
        val view = edgeViews.children.find { (it as EdgeView).source === source && (it as EdgeView).target === target } as EdgeView?

        view?.let {
            edgeViews.children -= view
        }

        return view
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
                serializedGraph.uiMetadata[it.node.id] = SerializablePoint2D(it.layoutX, it.layoutY)
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
        graph = DialogueGraphSerializer.fromSerializable(serializedGraph)

        nodeViews.children.clear()
        edgeViews.children.clear()

        graph.nodes.forEach {
            val x = serializedGraph.uiMetadata[it.id]?.x ?: 100.0
            val y = serializedGraph.uiMetadata[it.id]?.y ?: 100.0

            when (it.type) {
                DialogueNodeType.START -> {
                    addNodeView(StartNodeView(it), x, y)
                }

                DialogueNodeType.END -> {
                    addNodeView(EndNodeView(it), x, y)
                }

                DialogueNodeType.TEXT -> {
                    addNodeView(TextNodeView(it), x, y)
                }

                DialogueNodeType.CHOICE -> {
                    addNodeView(ChoiceNodeView(it), x, y)
                }

                DialogueNodeType.FUNCTION -> {
                    addNodeView(FunctionNodeView(it), x, y)
                }
                DialogueNodeType.BRANCH -> {
                    addNodeView(BranchNodeView(it), x, y)
                }

                else -> throw IllegalArgumentException("Unknown node type: ${it.type}")
            }
        }

        graph.edges.forEach { edge ->
            val source = nodeViews.children.map { it as NodeView }.find { it.node === edge.source }
            val target = nodeViews.children.map { it as NodeView }.find { it.node === edge.target }

            if (source != null && target != null) {
                val edgeView = source.outPoints[0].connect(target.inPoints[0])

                edgeViews.children.add(edgeView)
            }
        }

        graph.choiceEdges.forEach { edge ->
            val source = nodeViews.children.map { it as NodeView }.find { it.node === edge.source }
            val target = nodeViews.children.map { it as NodeView }.find { it.node === edge.target }

            if (source != null && target != null) {
                source.outPoints.find { it.choiceLocalID == edge.optionID }?.let { outPoint ->
                    val edgeView = outPoint.connect(target.inPoints[0])

                    edgeViews.children.add(edgeView)
                }
            }
        }
    }
}