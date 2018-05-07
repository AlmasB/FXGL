/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.view

import com.almasb.fxgl.core.Disposable
import com.almasb.fxgl.core.logging.Logger
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.shape.Circle

/**
 * Represents the visual aspect of an entity.
 * Note that the view need not be associated with an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
open class EntityView : Parent, Disposable {

    companion object {
        protected val log = Logger.get(EntityView::class.java)
    }

    /**
     * Returns nodes attached to this view.
     * Do NOT modify the list.
     *
     * @return list of children
     */
    val nodes: ObservableList<Node>
        get() = children

    constructor() {}

    /**
     * Constructs a view with given graphics content.
     *
     * @param graphics the view content
     */
    constructor(graphics: Node) {
        addNode(graphics)
    }

    /**
     * Add a child node to this view.
     *
     * @param node graphics
     */
    fun addNode(node: Node) {
        if (node is Circle) {
            node.centerX = node.radius
            node.centerY = node.radius
        }

        children.add(node)
    }

    /**
     * Removes a child node attached to this view.
     *
     * @param node graphics
     */
    fun removeNode(node: Node) {
        children.remove(node)
    }

    /**
     * Removes all attached nodes.
     */
    fun clearChildren() {
        children.clear()
    }

    override fun dispose() {
        // we only call dispose to let children to do manual cleanup
        // but we do not remove them from the parent
        // which would have been done by now by JavaFX
        for (n in children) {
            if (n is Disposable) {
                n.dispose();
            }
        }
    }
}
