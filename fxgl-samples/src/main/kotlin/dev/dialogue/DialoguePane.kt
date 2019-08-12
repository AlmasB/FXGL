/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.ui.FXGLScrollPane
import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane

//        startNode.outPoints.forEach {
//            it.localToSceneTransformProperty().addListener { _, _, newValue ->
//                println(newValue)
//            }
//        }

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialoguePane : HBox() {

    private val contentPane = Pane()

    private var selectedNodeView: NodeView? = null
    private var selectedOutLink: OutLinkPoint? = null


    private val graph = DialogueGraph()

    init {
        contentPane.setPrefSize(2000.0, 1000.0)

        val scroll = FXGLScrollPane(contentPane)
        scroll.style = "-fx-background-color: gray"
        //scroll.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        scroll.maxWidth = FXGL.getAppWidth() - 1.0
        scroll.maxHeight = FXGL.getAppHeight() - 1.0

        children.add(scroll)
        alignment = Pos.CENTER

        // start and end

        addNodeView(StartNodeView(), 100.0, 100.0)
        addNodeView(EndNodeView(), 400.0, 100.0)



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
            FS(true).writeDataTask(graph, "graph.dat").run()
        }

        val contextMenu = ContextMenu()
        contextMenu.items.addAll(item1, item2, btnSave)

        setOnContextMenuRequested {
            contextMenu.show(contentPane.scene.window, FXGL.getInput().mouseXUI + 200, FXGL.getInput().mouseYUI + 15)
        }
    }

    private fun addNodeView(nodeView: NodeView, x: Double = FXGL.getInput().mouseXUI, y: Double = FXGL.getInput().mouseYUI) {
        nodeView.relocate(x, y)

        graph.addNode(nodeView.node)

        attachMouseHandler(nodeView)

        contentPane.children.add(nodeView)
    }

    private fun attachMouseHandler(nodeView: NodeView) {
        nodeView.outPoints.forEach { p ->

            p.setOnMouseClicked {
                selectedOutLink = p as OutLinkPoint
                selectedNodeView = nodeView
            }
        }

        nodeView.inPoints.forEach { p ->

            p.setOnMouseClicked {
                selectedOutLink?.let {

                    val line = nodeView.connect(selectedNodeView!!, selectedOutLink!!, p as InLinkPoint)

                    if (it.choiceLocalID != -1) {
                        graph.addEdge(selectedNodeView!!.node as ChoiceNode, it.choiceLocalID, nodeView.node)
                    } else {
                        graph.addEdge(selectedNodeView!!.node, nodeView.node)
                    }

                    contentPane.children.add(line)

                    selectedOutLink = null
                    selectedNodeView = null
                }
            }
        }
    }
}