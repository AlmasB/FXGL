/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools

import com.almasb.fxgl.core.logging.Logger
import javafx.scene.Node
import javafx.scene.Parent
import jfxtras.util.NodeUtil
import org.controlsfx.tools.Utils

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
     * @return window in which [node] is located
     */
    fun getWindow(node: Node) = Utils.getWindow(node)

    /**
     * Remove [node] from its parent.
     *
     * @throws IllegalArgumentException if parent is unsupported
     */
    fun removeFromParent(node: Node) = NodeUtil.removeFromParent(node)

    /**
     * Add [node] to [parent].
     *
     * @throws IllegalArgumentException if parent is unsupported
     */
    fun addToParent(parent: Parent, node: Node) {
        NodeUtil.addToParent(parent, node)
    }
}