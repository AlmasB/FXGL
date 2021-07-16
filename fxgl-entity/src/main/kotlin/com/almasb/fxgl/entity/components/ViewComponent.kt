/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.View
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CoreComponent
import javafx.beans.property.*
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale

/**
 * Represents visual aspect of an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@CoreComponent
class ViewComponent : Component() {

    private val viewRoot = Group()

    // no scale or rotate is applied
    private val viewRootNoTransform = Group()
    private val devRoot = Group()

    private val updatableViews = arrayListOf<View>()

    /**
     * This node is managed by FXGL and is part of active scene graph, do NOT modify children.
     */
    val parent: Parent = Group()

    @get:JvmName("zIndexProperty")
    val zIndexProperty = SimpleIntegerProperty(0)

    @get:JvmName("opacityProperty")
    val opacityProperty = SimpleDoubleProperty(1.0)

    var opacity: Double
        get() = opacityProperty.value
        set(value) { opacityProperty.value = value }

    var zIndex: Int
        get() = zIndexProperty.value
        set(value) { zIndexProperty.value = value }

    @get:JvmName("visibleProperty")
    val visibleProperty: BooleanProperty
        get() = parent.visibleProperty()

    /**
     * If made invisible, all events (e.g. mouse) go through the entity
     * and do not register on the entity.
     * In contrast, changing opacity to 0 still allows the entity to receive
     * events (e.g. mouse clicks).
     */
    var isVisible: Boolean
        get() = parent.isVisible
        set(value) { parent.isVisible = value }

    /**
     * @return all view children (the order is transform applied first, then without transforms)
     */
    val children: List<Node>
        get() = viewRoot.children + viewRootNoTransform.children

    init {
        viewRoot.opacityProperty().bind(opacityProperty)
        viewRootNoTransform.opacityProperty().bind(viewRoot.opacityProperty())
    }

    override fun onAdded() {
        viewRoot.translateXProperty().bind(entity.xProperty().subtract(entity.transformComponent.positionOriginXProperty()))
        viewRoot.translateYProperty().bind(entity.yProperty().subtract(entity.transformComponent.positionOriginYProperty()))
        viewRoot.translateZProperty().bind(entity.zProperty().subtract(entity.transformComponent.positionOriginZProperty()))

        viewRootNoTransform.translateXProperty().bind(viewRoot.translateXProperty())
        viewRootNoTransform.translateYProperty().bind(viewRoot.translateYProperty())
        viewRootNoTransform.translateZProperty().bind(viewRoot.translateZProperty())

        devRoot.translateXProperty().bind(viewRoot.translateXProperty())
        devRoot.translateYProperty().bind(viewRoot.translateYProperty())
        devRoot.translateZProperty().bind(viewRoot.translateZProperty())

        val scale = Scale()
        scale.xProperty().bind(entity.transformComponent.scaleXProperty())
        scale.yProperty().bind(entity.transformComponent.scaleYProperty())
        scale.zProperty().bind(entity.transformComponent.scaleZProperty())

        scale.pivotXProperty().bind(entity.transformComponent.scaleOriginXProperty())
        scale.pivotYProperty().bind(entity.transformComponent.scaleOriginYProperty())
        scale.pivotZProperty().bind(entity.transformComponent.scaleOriginZProperty())

        val rz = Rotate()
        rz.axis = Rotate.Z_AXIS
        rz.angleProperty().bind(entity.transformComponent.angleProperty())

        rz.pivotXProperty().bind(entity.transformComponent.rotationOriginXProperty())
        rz.pivotYProperty().bind(entity.transformComponent.rotationOriginYProperty())
        rz.pivotZProperty().bind(entity.transformComponent.rotationOriginZProperty())

        val ry = Rotate(0.0, Rotate.Y_AXIS)
        ry.angleProperty().bind(entity.transformComponent.rotationYProperty())

        ry.pivotXProperty().bind(entity.transformComponent.rotationOriginXProperty())
        ry.pivotYProperty().bind(entity.transformComponent.rotationOriginYProperty())
        ry.pivotZProperty().bind(entity.transformComponent.rotationOriginZProperty())

        val rx = Rotate(0.0, Rotate.X_AXIS)
        rx.angleProperty().bind(entity.transformComponent.rotationXProperty())

        rx.pivotXProperty().bind(entity.transformComponent.rotationOriginXProperty())
        rx.pivotYProperty().bind(entity.transformComponent.rotationOriginYProperty())
        rx.pivotZProperty().bind(entity.transformComponent.rotationOriginZProperty())

        viewRoot.transforms.addAll(rz, ry, rx, scale)
        devRoot.transforms.addAll(rz, ry, rx, scale)
    }

    override fun onUpdate(tpf: Double) {
        updatableViews.forEach { it.onUpdate(tpf) }
    }

    override fun onRemoved() {
        (parent as Group).children.clear()
        clearChildren()
    }

    /**
     * Register event handler for event type that occurs on this view.
     */
    fun <T : Event> addEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>) {
        parent.addEventHandler(eventType, eventHandler)
    }

    fun addOnClickHandler(eventHandler: EventHandler<MouseEvent>) {
        addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler)
    }

    /**
     * Remove event handler for event type that occurs on this view.
     */
    fun <T : Event> removeEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>) {
        parent.removeEventHandler(eventType, eventHandler)
    }

    fun removeOnClickHandler(eventHandler: EventHandler<MouseEvent>) {
        removeEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler)
    }

    /**
     * Add a child node to this view.
     */
    @JvmOverloads fun addChild(node: Node, isTransformApplied: Boolean = true) {
        if (isTransformApplied) {
            addToGroup(viewRoot, node)
        } else {
            addToGroup(viewRootNoTransform, node)
        }

        if (node is View)
            updatableViews += node
    }

    /**
     * Remove a child from this view.
     */
    fun removeChild(node: Node) {
        removeFromGroup(viewRoot, node)
        removeFromGroup(viewRootNoTransform, node)

        if (node is View)
            updatableViews -= node
    }

    /**
     * Internal use only.
     */
    fun addDevChild(node: Node) {
        addToGroup(devRoot, node, true)
    }

    /**
     * Internal use only.
     */
    fun removeDevChild(node: Node) {
        removeFromGroup(devRoot, node)
    }

    private fun addToGroup(group: Group, child: Node, addLast: Boolean = false) {
        if (!(parent as Group).children.contains(group)) {
            if (addLast) {
                parent.children += group
            } else {
                parent.children.add(0, group)
            }
        }

        group.children += child
    }

    private fun removeFromGroup(group: Group, child: Node) {
        group.children -= child

        if (group.children.isEmpty()) {
            (parent as Group).children -= group
        }
    }

    /**
     * Remove all (with and without transforms) children.
     */
    fun clearChildren() {
        viewRoot.children.forEach {
            if (it is View) {
                updatableViews -= it

                it.dispose()
            }
        }

        viewRootNoTransform.children.forEach {
            if (it is View) {
                updatableViews -= it

                it.dispose()
            }
        }

        viewRoot.children.clear()
        viewRootNoTransform.children.clear()
    }

    override fun isComponentInjectionRequired(): Boolean = false
}
