package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.View
import com.almasb.fxgl.entity.EntityView
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CoreComponent
import javafx.beans.property.*
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@CoreComponent
class ViewComponent
@JvmOverloads constructor(initialView: View = EmptyView): Component() {

    /**
     * Only the first child is used when calling setView.
     * The other children can be used by systems such as debug bbox as necessary.
     */
    val parent = Group()

    val z: ReadOnlyIntegerProperty = ReadOnlyIntegerWrapper(0)

    @get:JvmName("opacityProperty")
    val opacity = SimpleDoubleProperty(1.0)

    private val propView: ObjectProperty<View> = SimpleObjectProperty<View>(initialView)

    var view: View
        get() = propView.value
        set(value) {

            if (parent.children.isEmpty()) {
                parent.children += value.node
            } else {
                parent.children[0] = value.node
            }

            propView.value = value
        }

    private val entityView by lazy { EntityView() }

    private val listeners = arrayListOf<ClickListener>()

    private val onClickListener = EventHandler<MouseEvent> { listeners.forEach { it.onClick() } }

    init {
        parent.opacityProperty().bind(opacity)

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
        parent.children.clear()
        view.dispose()
    }

    fun addClickListener(l: ClickListener) {
        listeners += l
    }

    fun removeClickListener(l: ClickListener) {
        listeners -= l
    }
}

interface ClickListener {
    fun onClick()
}

private object EmptyView : View {
    override fun onUpdate(tpf: Double) {
    }

    override fun getNode(): Node {
        return Pane()
    }

    override fun dispose() {
    }
}