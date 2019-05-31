package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.View
import com.almasb.fxgl.entity.EntityView
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
import javafx.scene.input.MouseEvent
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@CoreComponent
class ViewComponent
@JvmOverloads constructor(initialView: View = EmptyView()): Component() {

    /**
     * Only the first child is used when calling setView.
     * The other children can be used by systems such as debug bbox as necessary.
     * This node is managed by FXGL, do NOT modify children.
     */
    val parent: Parent = Group(initialView.node)

    val z: ReadOnlyIntegerProperty = ReadOnlyIntegerWrapper(0)

    @get:JvmName("opacityProperty")
    val opacityProp = SimpleDoubleProperty(1.0)

    var opacity: Double
        get() = opacityProp.value
        set(value) { opacityProp.value = value }

    var view: View = initialView
        set(value) {
            // unbind effects on old view
            view.node.opacityProperty().unbind()

            // apply effects to new view
            value.node.opacityProperty().bind(opacityProp)

            // attach to (possibly active) scene graph
            (parent as Group).children[0] = value.node

            field = value
        }

    /**
     * "Parent" of views created from Node since Node is not subtype of View.
     */
    private val entityView by lazy { EntityView() }

    private val listeners = arrayListOf<ClickListener>()

    private val onClickListener = EventHandler<MouseEvent> { listeners.forEach { it.onClick() } }

    init {
        parent.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickListener)
    }

    fun setViewFromNode(node: Node) {
        entityView.clearChildren()
        entityView.addNode(node)

        view = entityView
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
        view.onUpdate(tpf)
    }

    override fun onRemoved() {
        parent.removeEventHandler(MouseEvent.MOUSE_CLICKED, onClickListener)
        (parent as Group).children.clear()
        view.dispose()
    }

    @Deprecated(replaceWith = ReplaceWith("addEventHandler"), message = "")
    fun addClickListener(l: ClickListener) {
        listeners += l
    }

    @Deprecated(replaceWith = ReplaceWith("removeEventHandler"), message = "")
    fun removeClickListener(l: ClickListener) {
        listeners -= l
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
     * Add a child directly to parent on top of the actual view.
     * This is only used by FXGL itself.
     */
    fun addChild(node: Node) {
        (parent as Group).children += node
    }

    /**
     * Remove a child previously directly added to parent on top of the actual view.
     * This is only used by FXGL itself.
     */
    fun removeChild(node: Node) {
        (parent as Group).children -= node
    }
}

interface ClickListener {
    fun onClick()
}

/**
 * Dummy placeholder for ViewComponent.
 * Its getNode() will return a unique Node, so can be safely added to scene graph.
 */
private class EmptyView : View {
    override fun onUpdate(tpf: Double) {
    }

    override fun getNode(): Node {
        return Group()
    }

    override fun dispose() {
    }
}