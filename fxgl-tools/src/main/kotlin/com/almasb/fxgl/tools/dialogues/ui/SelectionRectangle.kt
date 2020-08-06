package com.almasb.fxgl.tools.dialogues.ui

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SelectionRectangle : Rectangle() {



    init {
        fill = Color.web("darkblue", 0.4)
        stroke = Color.LIGHTBLUE
        isVisible = false
    }

    /**
     * @return partially or fully selected (overlapping) nodes in [root] with [type]
     */
    fun <T : Node> getSelectedNodesIn(root: Parent, type: Class<T>): List<T> {
        //println("Parent bounds: $boundsInParent")


        return root.childrenUnmodifiable
                .filterIsInstance(type)
                .filter {
                    //println("Node: ${it.boundsInParent}")

                    it.boundsInParent.intersects(boundsInParent)
                }
    }
}