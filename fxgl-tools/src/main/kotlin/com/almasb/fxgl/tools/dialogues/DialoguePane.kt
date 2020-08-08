/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.cutscene.dialogue.*
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.tools.dialogues.ui.FXGLContextMenu
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.texture.toImage
import com.almasb.fxgl.tools.dialogues.DialogueEditorVars.IS_SNAP_TO_GRID
import com.almasb.fxgl.tools.dialogues.ui.SelectionRectangle
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ListChangeListener
import javafx.collections.MapChangeListener
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.effect.Glow
import javafx.scene.input.MouseButton
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Scale
import javafx.util.Duration
import kotlin.math.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialoguePane(graph: DialogueGraph = DialogueGraph()) : Pane() {

    companion object {
        private val log = Logger.get<DialoguePane>()

        private val branch: (DialogueNode) -> NodeView = { BranchNodeView(it) }
        private val end: (DialogueNode) -> NodeView = { EndNodeView(it) }
        private val start: (DialogueNode) -> NodeView = { StartNodeView(it) }
        private val function: (DialogueNode) -> NodeView = { FunctionNodeView(it) }
        private val text: (DialogueNode) -> NodeView = { TextNodeView(it) }
        private val subdialogue: (DialogueNode) -> NodeView = { SubDialogueNodeView(it) }
        private val choice: (DialogueNode) -> NodeView = { ChoiceNodeView(it) }

        val nodeConstructors = linkedMapOf<DialogueNodeType, () -> DialogueNode>(
                TEXT to { TextNode("") },
                CHOICE to { ChoiceNode("") },
                BRANCH to { BranchNode("") },
                FUNCTION to { FunctionNode("") },
                END to { EndNode("") },
                START to { StartNode("") },
                SUBDIALOGUE to { SubDialogueNode("") }
        )

        val nodeViewConstructors = linkedMapOf<DialogueNodeType, (DialogueNode) -> NodeView>(
                TEXT to text,
                CHOICE to choice,
                BRANCH to branch,
                FUNCTION to function,
                END to end,
                START to start,
                SUBDIALOGUE to subdialogue
        )

        private const val CELL_SIZE = 39.0
        private const val MARGIN_CELLS = 3
        private const val CELL_DISTANCE = CELL_SIZE + 1.0
    }

    private val contentRoot = Group()

    private var selectedOutLink: OutLinkPoint? = null

    var graph = graph
        private set

    private val views = Group()
    private val edgeViews = Group()
    private val nodeViews = Group()

    val isDirtyProperty = SimpleBooleanProperty(false)

    private val scale = Scale()

    private val dragScale = 1.35

    private var mouseX = 0.0
    private var mouseY = 0.0

    private val mouseGestures = MouseGestures(contentRoot)

    private val selectionRect = SelectionRectangle()

    private var selectionStart = Point2D.ZERO
    private var isSelectingRectangle = false

    private val selectedNodeViews = arrayListOf<NodeView>()

    init {
        setPrefSize(getAppWidth().toDouble(), getAppHeight().toDouble())

        val cell = Rectangle(CELL_SIZE - 1, CELL_SIZE - 1, Color.GRAY)
        cell.stroke = Color.WHITESMOKE
        cell.strokeWidth = 0.2

        val image = toImage(cell)

        val bgGrid = Region()
        bgGrid.background = Background(BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)
        )
        bgGrid.isMouseTransparent = true

        contentRoot.children.addAll(
                bgGrid, edgeViews, views, nodeViews, selectionRect
        )

        contentRoot.transforms += scale

        setOnScroll {
            val scaleFactor = if (it.deltaY < 0) 0.95 else 1.05

            scale.x *= scaleFactor
            scale.y *= scaleFactor

            contentRoot.translateX += it.sceneX * (1 - scaleFactor) * scale.x
            contentRoot.translateY += it.sceneY * (1 - scaleFactor) * scale.y
        }

        children.addAll(contentRoot)

        // start and end

        createNode(StartNodeView(), 50.0, getAppHeight() / 2.0)
        createNode(EndNodeView(), getAppWidth() - 370.0, getAppHeight() / 2.0)

        initContextMenu()

        mouseGestures.makeDraggable(selectionRect) {
            selectionStart = Point2D(selectionRect.layoutX, selectionRect.layoutY)
        }

        selectionRect.layoutXProperty().addListener { _, prevX, layoutX ->
            val dx = layoutX.toDouble() - prevX.toDouble()

            selectedNodeViews.forEach {
                it.layoutX += dx
            }
        }
        selectionRect.layoutYProperty().addListener { _, prevY, layoutY ->
            val dy = layoutY.toDouble() - prevY.toDouble()

            selectedNodeViews.forEach {
                it.layoutY += dy
            }
        }

        setOnMouseMoved {
            mouseX = it.sceneX
            mouseY = it.sceneY
        }

        setOnMouseDragged {
            if (it.isControlDown && isSelectingRectangle) {
                val vector = contentRoot.sceneToLocal(it.sceneX, it.sceneY).subtract(selectionStart)

                selectionRect.width = vector.x
                selectionRect.height = vector.y

                return@setOnMouseDragged
            }

            if (mouseGestures.isDragging || it.button != MouseButton.PRIMARY)
                return@setOnMouseDragged

            contentRoot.translateX += (it.sceneX - mouseX) * dragScale
            contentRoot.translateY += (it.sceneY - mouseY) * dragScale

            mouseX = it.sceneX
            mouseY = it.sceneY
        }

        setOnMousePressed {
            if (it.isControlDown) {
                isSelectingRectangle = true
                selectedNodeViews.clear()
                selectionStart = contentRoot.sceneToLocal(it.sceneX, it.sceneY)
                selectionRect.layoutX = selectionStart.x
                selectionRect.layoutY = selectionStart.y
                selectionRect.width = 0.0
                selectionRect.height = 0.0
                selectionRect.isVisible = true
            }
        }

        setOnMouseReleased {
            if (!isSelectingRectangle) {
                return@setOnMouseReleased
            }

            isSelectingRectangle = false
            selectedNodeViews.addAll(selectionRect.getSelectedNodesIn(nodeViews, NodeView::class.java))

            if (selectedNodeViews.isEmpty()) {
                selectionRect.isVisible = false
            }
        }

        initGraphListeners()

        initGridListener(bgGrid)
    }

    private fun initContextMenu() {
        val contextMenu = FXGLContextMenu()

        nodeConstructors
                .filter { it.key != START }
                .forEach { (type, ctor) ->
                    contextMenu.addItem(type.toString()) {
                        graph.addNode(ctor())
                    }
                }

        setOnContextMenuRequested {
            if (it.target !== this)
                return@setOnContextMenuRequested

            contextMenu.show(contentRoot, it.sceneX, it.sceneY)
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

    private fun initGridListener(bg: Region) {
        run({
            var minX = Double.MAX_VALUE
            var minY = Double.MAX_VALUE
            var maxX = -Double.MAX_VALUE
            var maxY = -Double.MAX_VALUE

            nodeViews.children
                    .map { it as NodeView }
                    .forEach {
                        minX = min(it.layoutX, minX)
                        minY = min(it.layoutY, minY)
                        maxX = max(it.layoutX + it.prefWidth, maxX)
                        maxY = max(it.layoutY + it.prefHeight, maxY)
                    }

            bg.layoutX = (minX / CELL_DISTANCE).toInt() * CELL_DISTANCE - MARGIN_CELLS * CELL_DISTANCE
            bg.layoutY = (minY / CELL_DISTANCE).toInt() * CELL_DISTANCE - MARGIN_CELLS * CELL_DISTANCE

            bg.prefWidth = ((maxX - bg.layoutX) / CELL_DISTANCE).toInt() * CELL_DISTANCE + MARGIN_CELLS * CELL_DISTANCE
            bg.prefHeight = ((maxY - bg.layoutY) / CELL_DISTANCE).toInt() * CELL_DISTANCE + MARGIN_CELLS * CELL_DISTANCE
        }, Duration.seconds(0.1))

        // TODO: better kt call site?
        FXGL.getWorldProperties().addListener<Boolean>(IS_SNAP_TO_GRID, object : PropertyChangeListener<Boolean> {
            override fun onChange(prev: Boolean, now: Boolean) {
                if (now) {
                    nodeViews.children
                            .map { it as NodeView }
                            .forEach { snapToGrid(it) }
                }
            }
        })
    }

    private fun onAdded(node: DialogueNode) {
        val p = contentRoot.sceneToLocal(mouseX, mouseY)

        onAdded(node, p.x, p.y)
    }

    private fun onAdded(node: DialogueNode, x: Double, y: Double) {
        log.debug("Added node: $node")

        isDirtyProperty.value = true

        val nodeViewConstructor = nodeViewConstructors[node.type] ?: throw IllegalArgumentException("View constructor for ${node.type} does not exist")
        val nodeView = nodeViewConstructor(node)

        addNodeView(nodeView, x, y)
    }

    private fun onRemoved(node: DialogueNode) {
        log.debug("Removed node: $node")

        isDirtyProperty.value = true

        val nodeView = getNodeView(node)

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
        log.debug("Added edge: $edge")

        isDirtyProperty.value = true

        val (outPoint, inPoint) = if (edge is DialogueChoiceEdge) {
            getNodeView(edge.source).outPoints.find { it.choiceOptionID == edge.optionID }!! to getNodeView(edge.target).inPoint!!
        } else {
            getNodeView(edge.source).outPoints.first() to getNodeView(edge.target).inPoint!!
        }

        outPoint.connect(inPoint)

        val edgeView = EdgeView(edge, outPoint, inPoint)

        edgeViews.children.add(edgeView)
    }

    private fun onRemoved(edge: DialogueEdge) {
        log.debug("Removed edge: $edge")

        isDirtyProperty.value = true

        val edgeView = getEdgeView(edge)

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

    private fun getNodeView(node: DialogueNode): NodeView {
        return nodeViews.children
                .map { it as NodeView }
                .find { it.node === node }
                ?: throw IllegalArgumentException("No view found for node $node")
    }

    private fun getEdgeView(edge: DialogueEdge): EdgeView {
        val optionID = if (edge is DialogueChoiceEdge) edge.optionID else -1

        return edgeViews.children
                .map { it as EdgeView }
                .find { it.source.owner.node === edge.source && it.optionID == optionID && it.target.owner.node === edge.target }
                ?: throw IllegalArgumentException("No edge view found for edge $edge")
    }

    private fun createNode(nodeView: NodeView, x: Double, y: Double) {
        graph.addNode(nodeView.node)

        addNodeView(nodeView, x, y)
    }

    private fun addNodeView(nodeView: NodeView, x: Double, y: Double) {
        nodeView.relocate(x, y)

        attachMouseHandler(nodeView)

        nodeViews.children.add(nodeView)

        // START node cannot be removed
        if (nodeView.node.type == START) {
            nodeView.closeButton.isVisible = false
        }
    }

    private fun attachMouseHandler(nodeView: NodeView) {
        mouseGestures.makeDraggable(nodeView) {
            if (getb(IS_SNAP_TO_GRID))
                snapToGrid(nodeView)
        }

        nodeView.closeButton.setOnMouseClicked {
            graph.removeNode(nodeView.node)
        }

        nodeView.outPoints.forEach { outPoint ->
            attachMouseHandler(outPoint)
        }

        nodeView.outPoints.addListener { c: ListChangeListener.Change<out OutLinkPoint> ->
            while (c.next()) {
                c.addedSubList.forEach { outPoint ->
                    attachMouseHandler(outPoint)
                }
            }
        }

        nodeView.inPoint?.let { inPoint ->
            attachMouseHandler(inPoint)
        }
    }

    private fun attachMouseHandler(outPoint: OutLinkPoint) {
        outPoint.setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY) {
                selectedOutLink = outPoint
            } else {
                if (outPoint.isConnected) {
                    disconnectOutLink(outPoint)
                }
            }
        }
    }

    private fun attachMouseHandler(inPoint: InLinkPoint) {
        inPoint.setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY) {
                selectedOutLink?.let { outPoint ->

                    if (outPoint.isConnected) {
                        disconnectOutLink(outPoint)
                    }

                    if (outPoint.choiceOptionID != -1) {
                        graph.addChoiceEdge(outPoint.owner.node, outPoint.choiceOptionID, inPoint.owner.node)
                    } else {
                        graph.addEdge(outPoint.owner.node, inPoint.owner.node)
                    }

                    // reset selection
                    selectedOutLink = null
                }
            }
        }
    }

    private fun snapToGrid(nodeView: NodeView) {
        nodeView.layoutX = (nodeView.layoutX / CELL_DISTANCE).roundToInt() * CELL_DISTANCE
        nodeView.layoutY = (nodeView.layoutY / CELL_DISTANCE).roundToInt() * CELL_DISTANCE
    }

    private fun disconnectOutLink(outPoint: OutLinkPoint) {
        outPoint.other?.let { inPoint ->
            if (outPoint.choiceOptionID != -1) {
                graph.removeChoiceEdge(outPoint.owner.node, outPoint.choiceOptionID, inPoint.owner.node)
            } else {
                graph.removeEdge(outPoint.owner.node, inPoint.owner.node)
            }
        }
    }

    fun save(): SerializableGraph {
        isDirtyProperty.value = false

        val serializedGraph = DialogueGraphSerializer.toSerializable(graph)

        nodeViews.children.map { it as NodeView }.forEach {
            serializedGraph.uiMetadata[graph.findNodeID(it.node)] = SerializablePoint2D(it.layoutX, it.layoutY)
        }

        return serializedGraph
    }

    fun load(serializedGraph: SerializableGraph) {
        log.info("Loaded graph with version=${serializedGraph.version}")

        graph = DialogueGraphSerializer.fromSerializable(serializedGraph)

        nodeViews.children.clear()
        edgeViews.children.clear()

        graph.nodes.forEach { (id, node) ->
            val x = serializedGraph.uiMetadata[id]?.x ?: 100.0
            val y = serializedGraph.uiMetadata[id]?.y ?: 100.0

            onAdded(node, x, y)
        }

        graph.edges.forEach { edge ->
            onAdded(edge)
        }

        initGraphListeners()

        isDirtyProperty.value = false
    }
}