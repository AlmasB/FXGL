/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getAppHeight
import com.almasb.fxgl.dsl.getAppWidth
import com.almasb.fxgl.dsl.getGameController
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.scene.Group
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.Slider
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

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

    private val dragScale = 1.25
    private var dragX = 0.0
    private var dragY = 0.0

    init {
        setPrefSize(getAppWidth().toDouble(), getAppHeight().toDouble())
        style = "-fx-background-color: gray"

        val btnRun = Text("Run")
        btnRun.fill = Color.WHITE
        btnRun.font = Font.font(28.0)
        btnRun.setOnMouseClicked {
            DialogueScene(getGameController(), getAppWidth(), getAppHeight()).start(graph)
        }

        contentRoot.children.add(StackPane(
                Rectangle(80.0, 40.0, Color.color(0.0, 0.0, 0.0, 0.5)),
                btnRun
        ))

        contentRoot.children += edgeViews

//        val scroll = FXGLScrollPane(contentPane)
//        scroll
//        scroll.maxWidth = FXGL.getAppWidth() - 1.0
//        scroll.maxHeight = FXGL.getAppHeight() - 45.0


        val slider = Slider(0.1, 2.0, 1.0)
        slider.isShowTickMarks = true
        slider.isShowTickLabels = true
        //slider.majorTickUnit = 0.5
        //slider.blockIncrement = 0.1
        slider.setPrefSize(100.0, 40.0)
        slider.translateY = FXGL.getAppHeight() - 40.0
        contentRoot.scaleXProperty().bind(slider.valueProperty())
        contentRoot.scaleYProperty().bind(slider.valueProperty())

        children.addAll(contentRoot, slider)

        // start and end

        addNodeView(StartNodeView(), 100.0, 100.0)
        addNodeView(EndNodeView(), 600.0, 100.0)



        val item1 = MenuItem("Text")
        item1.setOnAction {
            val textNode = TextNodeView()

            addNodeView(textNode)
        }

        val item2 = MenuItem("Choice")
        item2.setOnAction {
            val textNode = ChoiceNodeView()

            addNodeView(textNode)
        }

        val btnSave = MenuItem("Save")
        btnSave.setOnAction {
            val mapper = jacksonObjectMapper()
            mapper.enable(SerializationFeature.INDENT_OUTPUT)

            val s = mapper.writeValueAsString(graph.toSerializable())
            println(s)

            val graph2 = mapper.readValue(s, SerializableGraph::class.java).toGraph()

            println()
            println(graph2)
        }

        val contextMenu = ContextMenu()
        contextMenu.items.addAll(item1, item2, btnSave)

        setOnContextMenuRequested {
            contextMenu.show(contentRoot.scene.window, FXGL.getInput().mouseXUI + 200, FXGL.getInput().mouseYUI + 15)
        }



        setOnMouseMoved {
            dragX = it.x
            dragY = it.y
        }

        setOnMouseDragged {
            if (!it.isControlDown)
                return@setOnMouseDragged

            contentRoot.translateX += (it.x - dragX) * dragScale
            contentRoot.translateY += (it.y - dragY) * dragScale

            dragX = it.x
            dragY = it.y
        }
    }

    private fun addNodeView(nodeView: NodeView, x: Double = FXGL.getInput().mouseXUI, y: Double = FXGL.getInput().mouseYUI) {
        nodeView.relocate(x, y)

        graph.addNode(nodeView.node)

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
                        outPoint.disconnect()?.let { inPoint ->
                            val view = removeEdgeView(outPoint, inPoint)

                            view?.let {
                                if (it.localID != -1) {
                                    graph.removeEdge(nodeView.node, it.localID, inPoint.owner.node)
                                } else {
                                    graph.removeEdge(nodeView.node, inPoint.owner.node)
                                }
                            }
                        }
                    }
                }
            }
        }

        nodeView.inPoints.forEach { p ->

            p.setOnMouseClicked {

                if (it.button == MouseButton.PRIMARY) {

                    selectedOutLink?.let { outLink ->

                        val edgeView = outLink.connect(p)

                        edgeView?.let {
                            if (outLink.choiceLocalID != -1) {
                                edgeView.localID = outLink.choiceLocalID
                                graph.addEdge(selectedNodeView!!.node as ChoiceNode, outLink.choiceLocalID, outLink.choiceLocalOptionProperty.value, nodeView.node)
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

    private fun removeEdgeView(source: OutLinkPoint, target: InLinkPoint): EdgeView? {
        val view = edgeViews.children.find { (it as EdgeView).source === source && (it as EdgeView).target === target } as EdgeView?

        view?.let {
            edgeViews.children -= view
        }

        return view
    }
}