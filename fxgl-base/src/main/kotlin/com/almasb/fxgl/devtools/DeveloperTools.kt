/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools

import com.almasb.fxgl.core.logging.Logger
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane

/**
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
object DeveloperTools {

    private val log = Logger.get("FXGLDeveloperTools")

    /**
     * Recursively counts number of children of [node].
     */
    fun getChildrenSize(node: Node): Int {
        log.debug("Counting children for $node")

        when (node) {
            is Parent -> return node.childrenUnmodifiable.size + node.childrenUnmodifiable.map { getChildrenSize(it) }.sum()
            else      -> return 0
        }
    }

    /**
     * Removes the specified node from its parent.
     *
     * @param n the node to remove
     *
     * @throws IllegalArgumentException if an unsupported parent class has been
     * specified or the parent is `null`
     */
    fun removeFromParent(n: Node) {
        if (n.parent is Group) {
            (n.parent as Group).children.remove(n)
        } else if (n.parent is Pane) {
            (n.parent as Pane).children.remove(n)
        } else {
            throw IllegalArgumentException("Unsupported parent: " + n.parent)
        }
    }

    /**
     * Adds the given node to the specified parent.
     *
     * @param p parent
     * @param n node
     *
     * @throws IllegalArgumentException if an unsupported parent class has been
     * specified or the parent is `null`
     */
    fun addToParent(p: Parent, n: Node) {
        (p as? Group)?.children?.add(n)
                ?: ((p as? Pane)?.children?.add(n) ?: throw IllegalArgumentException("Unsupported parent: $p"))
    }
}