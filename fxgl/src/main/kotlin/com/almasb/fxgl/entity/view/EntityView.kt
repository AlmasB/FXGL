/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.view

import com.almasb.fxgl.core.Disposable
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.entity.RenderLayer
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
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

    private val renderLayer = SimpleObjectProperty(RenderLayer.TOP)

    /**
     * Constructs a view with no content.
     */
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
     * Constructs a view with given render layer.
     *
     * @param layer render layer
     */
    constructor(layer: RenderLayer) {
        setRenderLayer(layer)
    }

    /**
     * Constructs a view with given graphics and render layer.
     *
     * @param graphics content
     * @param layer render layer
     */
    constructor(graphics: Node, layer: RenderLayer) {
        addNode(graphics)
        setRenderLayer(layer)
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

    /**
     * Set render layer for this entity.
     * Render layer determines how an entity
     * is rendered relative to other entities.
     * The layer with higher index()
     * will be rendered on top of the layer with lower index().
     * By default an
     * entity has the very top layer with highest index equal to
     * [Integer.MAX_VALUE].
     *
     * @param renderLayer the render layer
     */
    fun setRenderLayer(renderLayer: RenderLayer) {
        this.renderLayer.set(renderLayer)
    }

    /**
     * @return render layer
     */
    fun getRenderLayer(): RenderLayer {
        return renderLayer.get()
    }

    /**
     * @return render layer property
     */
    fun renderLayerProperty(): ObjectProperty<RenderLayer> {
        return renderLayer
    }

    override fun dispose() {
        // we only call dispose to let children to do manual cleanup
        // but we do not remove them from the parent
        // which would have been done by now by JavaFX
        children.stream()
                .filter { n -> n is Disposable }
                .map { n -> n as Disposable }
                .forEach { it.dispose() }
    }
}
