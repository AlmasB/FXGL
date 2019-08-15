/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.getAppHeight
import com.almasb.fxgl.dsl.getAppWidth
import com.almasb.fxgl.dsl.getGameController
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.beans.binding.Bindings
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
import sandbox.cutscene.MouseGestures

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialoguePane : Pane() {

    private val contentRoot = Group()

    private var selectedNodeView: NodeView? = null
    private var selectedOutLink: OutLinkPoint? = null

    private val graph = DialogueGraph()

    private val edgeViews = Group()

    private val dragScale = 1.35
    private var dragX = 0.0
    private var dragY = 0.0

    private var mouseX = 0.0
    private var mouseY = 0.0

    private val mouseGestures = MouseGestures(contentRoot)

    private val toolbar = HBox(5.0)

    init {
        toolbar.setPrefSize(getAppWidth().toDouble(), 30.0)
        toolbar.translateY = -toolbar.prefHeight
        toolbar.style = "-fx-background-color: black"


        val itemSave = MenuItem("Save")
        itemSave.setOnAction {
            val mapper = jacksonObjectMapper()
            mapper.enable(SerializationFeature.INDENT_OUTPUT)

            val s = mapper.writeValueAsString(graph.toSerializable())
            println(s)

            val graph2 = mapper.readValue(s, SerializableGraph::class.java).toGraph()

            println()
            println(graph2)
        }

        val menuFile = Menu("")
        menuFile.graphic = Text("File").also { it.fill = Color.WHITE }
        menuFile.style = "-fx-background-color: black"
        menuFile.items.addAll(itemSave, MenuItem("Load"))

        val menuBar = MenuBar()
        menuBar.style = "-fx-background-color: black"
        menuBar.menus.addAll(menuFile)

        toolbar.children += menuBar


        translateY = toolbar.prefHeight
        setPrefSize(getAppWidth().toDouble(), getAppHeight().toDouble())
        style = "-fx-background-color: gray"


        toolbar.children += makeRunButton()

        contentRoot.children.addAll(
                edgeViews
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

        children.addAll(toolbar, contentRoot)

        // start and end

        addNodeView(StartNodeView(), 50.0, getAppHeight() / 2.0)
        addNodeView(EndNodeView(), getAppWidth() - 370.0, getAppHeight() / 2.0)



        val item1 = MenuItem("Text")
        item1.setOnAction {
            val textNode = TextNodeView()

            val p = contentRoot.sceneToLocal(mouseX, mouseY)

            addNodeView(textNode, p.x, p.y)
        }

        val item2 = MenuItem("Choice")
        item2.setOnAction {
            val textNode = ChoiceNodeView()

            val p = contentRoot.sceneToLocal(mouseX, mouseY)

            addNodeView(textNode, p.x, p.y)
        }


        val contextMenu = ContextMenu()
        contextMenu.items.addAll(item1, item2)

        setOnContextMenuRequested {
            if (it.target !== this)
                return@setOnContextMenuRequested

            contextMenu.show(contentRoot.scene.window, it.sceneX + 150.0, it.sceneY + 45.0)
        }

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

    private fun addNodeView(nodeView: NodeView, x: Double, y: Double) {
        nodeView.relocate(x, y)

        graph.addNode(nodeView.node)

        mouseGestures.makeDraggable(nodeView)
        attachMouseHandler(nodeView)

        contentRoot.children.add(nodeView)
    }

    private fun attachMouseHandler(nodeView: NodeView) {
        nodeView.outPoints.forEach { outPoint ->

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

        nodeView.inPoints.forEach { inPoint ->

            inPoint.setOnMouseClicked {

                if (it.button == MouseButton.PRIMARY) {

                    selectedOutLink?.let { outPoint ->

                        if (outPoint.isConnected) {
                            disconnectOutLink(outPoint)
                        }

                        val edgeView = outPoint.connect(inPoint)

                        edgeView?.let {
                            if (outPoint.choiceLocalID != -1) {
                                edgeView.localID = outPoint.choiceLocalID
                                graph.addEdge(selectedNodeView!!.node as ChoiceNode, outPoint.choiceLocalID, outPoint.choiceLocalOptionProperty.value, nodeView.node)
                            } else {
                                graph.addEdge(selectedNodeView!!.node, nodeView.node)
                            }

                            edgeViews.children.add(edgeView)
                        }

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
}