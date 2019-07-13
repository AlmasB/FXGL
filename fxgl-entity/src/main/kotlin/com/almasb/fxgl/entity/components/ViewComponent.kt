package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.View
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CoreComponent
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.ReadOnlyIntegerWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
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
    internal val devRoot = Group()

    private val updateableViews = arrayListOf<View>()

    /**
     * This node is managed by FXGL and is part of active scene graph, do NOT modify children.
     */
    val parent: Parent = Group(viewRoot, devRoot)

    val z: ReadOnlyIntegerProperty = ReadOnlyIntegerWrapper(0)

    @get:JvmName("opacityProperty")
    val opacityProp = SimpleDoubleProperty(1.0)

    var opacity: Double
        get() = opacityProp.value
        set(value) { opacityProp.value = value }

    /**
     * Do not modify children, for queries only.
     */
    val children: List<Node>
        get() = viewRoot.children

    init {
        viewRoot.opacityProperty().bind(opacityProp)
    }

    override fun onAdded() {
        parent.translateXProperty().bind(entity.xProperty().subtract(entity.transformComponent.positionOriginXProperty()))
        parent.translateYProperty().bind(entity.yProperty().subtract(entity.transformComponent.positionOriginYProperty()))

        val scale = Scale()
        scale.xProperty().bind(entity.transformComponent.scaleXProperty())
        scale.yProperty().bind(entity.transformComponent.scaleYProperty())

        scale.pivotXProperty().bind(entity.transformComponent.scaleOriginXProperty())
        scale.pivotYProperty().bind(entity.transformComponent.scaleOriginYProperty())

        val rotate = Rotate()
        rotate.axis = Rotate.Z_AXIS
        rotate.angleProperty().bind(entity.transformComponent.angleProperty())

        rotate.pivotXProperty().bind(entity.transformComponent.rotationOriginXProperty())
        rotate.pivotYProperty().bind(entity.transformComponent.rotationOriginYProperty())

        parent.transforms.addAll(rotate, scale)

        (z as ReadOnlyIntegerWrapper).bind(entity.transformComponent.zProperty())
    }

    override fun onUpdate(tpf: Double) {
        updateableViews.forEach { it.onUpdate(tpf) }
    }

    override fun onRemoved() {
        (parent as Group).children.clear()
        viewRoot.children.forEach {
            if (it is View) {
                it.dispose()
            }
        }
    }

    /**
     * Register event handler for event type that occurs on this view.
     */
    fun <T : Event> addEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>) {
        parent.addEventHandler(eventType, eventHandler)
    }

    /**
     * Remove event handler for event type that occurs on this view.
     */
    fun <T : Event> removeEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>) {
        parent.removeEventHandler(eventType, eventHandler)
    }

    /**
     * Add a child node to this view.
     */
    fun addChild(node: Node) {
        viewRoot.children += node

        if (node is View)
            updateableViews += node
    }

    /**
     * Remove a child from this view.
     */
    fun removeChild(node: Node) {
        viewRoot.children -= node

        if (node is View)
            updateableViews -= node
    }

    fun clearChildren() {
        viewRoot.children.forEach {
            if (it is View) {
                updateableViews -= it

                // TODO: it.dispose()
            }
        }

        viewRoot.children.clear()
    }
}