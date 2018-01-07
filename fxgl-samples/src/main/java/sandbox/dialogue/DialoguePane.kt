/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import com.almasb.fxgl.app.FXGL
import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialoguePane : HBox() {

    private val contentPane = Pane()

    private var selectedNodeView: NodeView? = null
    private var selectedOutLink: OutLinkPoint? = null

    init {
        contentPane.setPrefSize(2000.0, 1000.0)

        val scroll = ScrollPane(contentPane)
        //scroll.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        scroll.maxWidth = FXGL.getAppWidth() - 1.0
        scroll.maxHeight = FXGL.getAppHeight() - 1.0

        children.add(scroll)
        alignment = Pos.CENTER

        // start and end

        val startNode = StartNodeView()
        startNode.relocate(100.0, 100.0)

//        startNode.outPoints.forEach {
//            it.localToSceneTransformProperty().addListener { _, _, newValue ->
//                println(newValue)
//            }
//        }

        val endNode = EndNodeView()
        endNode.relocate(400.0, 100.0)












        contentPane.children.addAll(startNode, endNode)



        val contextMenu = ContextMenu()

        val item1 = MenuItem("Text")
        item1.setOnAction {
            val textNode = TextNodeView()
            textNode.relocate(FXGL.getInput().getMouseXUI(), FXGL.getInput().getMouseYUI())

            attachMouseHandler(textNode)

            contentPane.children.add(textNode)
        }

        val item2 = MenuItem("Function")
        item2.setOnAction {
            val fNode = ScriptNodeView()
            fNode.relocate(FXGL.getInput().getMouseXUI(), FXGL.getInput().getMouseYUI())

            attachMouseHandler(fNode)

            contentPane.children.add(fNode)
        }

        val item3 = MenuItem("Choice")
        item3.setOnAction {
            val fNode = ChoiceNodeView()
            fNode.relocate(FXGL.getInput().getMouseXUI(), FXGL.getInput().getMouseYUI())

            attachMouseHandler(fNode)

            contentPane.children.add(fNode)
        }

        contextMenu.items.addAll(item1, item2, item3)




        setOnContextMenuRequested {
            contextMenu.show(contentPane.scene.window, FXGL.getInput().getMouseXUI() + 200, FXGL.getInput().getMouseYUI() + 15)
        }



        attachMouseHandler(startNode)
        attachMouseHandler(endNode)
    }

    private fun attachMouseHandler(nodeView: NodeView) {
        nodeView.outPoints.forEach { p ->

            println("attached out")

            p.setOnMouseClicked {
                selectedOutLink = p as OutLinkPoint
                selectedNodeView = nodeView
            }
        }

        nodeView.inPoints.forEach { p ->

            println("attached in")

            p.setOnMouseClicked {
                selectedOutLink?.let {

                    val line = nodeView.connect(selectedNodeView!!, selectedOutLink!!, p as InLinkPoint)

                    contentPane.children.add(line)

                    selectedOutLink = null
                    selectedNodeView = null
                }
            }
        }
    }
}