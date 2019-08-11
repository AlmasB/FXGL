/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.FXGL
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

        val startNode = StartNodeView()
        startNode.relocate(100.0, 100.0)

        val endNode = EndNodeView()
        endNode.relocate(400.0, 100.0)

        graph.addNode(startNode.node)
        graph.addNode(endNode.node)

        contentPane.children.addAll(startNode, endNode)








        val contextMenu = ContextMenu()

        val item1 = MenuItem("Text")
        item1.setOnAction {
            val textNode = TextNodeView()
            textNode.relocate(FXGL.getInput().mouseXUI, FXGL.getInput().mouseYUI)

            graph.addNode(textNode.node)

            attachMouseHandler(textNode)

            contentPane.children.add(textNode)
        }

        contextMenu.items.addAll(item1)

        setOnContextMenuRequested {
            contextMenu.show(contentPane.scene.window, FXGL.getInput().mouseXUI + 200, FXGL.getInput().mouseYUI + 15)
        }





//
//        val item2 = MenuItem("Function")
//        item2.setOnAction {
//            val fNode = ScriptNodeView()
//            fNode.relocate(FXGL.getInput().getMouseXUI(), FXGL.getInput().getMouseYUI())
//
//            attachMouseHandler(fNode)
//
//            contentPane.children.add(fNode)
//        }
//
//        val item3 = MenuItem("Choice")
//        item3.setOnAction {
//            val fNode = ChoiceNodeView()
//            fNode.relocate(FXGL.getInput().getMouseXUI(), FXGL.getInput().getMouseYUI())
//
//            attachMouseHandler(fNode)
//
//            contentPane.children.add(fNode)
//        }
//




        attachMouseHandler(startNode)
        attachMouseHandler(endNode)
    }

    private fun attachMouseHandler(nodeView: NodeView) {
        nodeView.outPoints.forEach { p ->

            //println("attached out")

            p.setOnMouseClicked {
                selectedOutLink = p as OutLinkPoint
                selectedNodeView = nodeView
            }
        }

        nodeView.inPoints.forEach { p ->

            //println("attached in")

            p.setOnMouseClicked {
                selectedOutLink?.let {

                    val line = nodeView.connect(selectedNodeView!!, selectedOutLink!!, p as InLinkPoint)

                    graph.addEdge(selectedNodeView!!.node, nodeView.node)

                    contentPane.children.add(line)

                    selectedOutLink = null
                    selectedNodeView = null
                }
            }
        }
    }
}