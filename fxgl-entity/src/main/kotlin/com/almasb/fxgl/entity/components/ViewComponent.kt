package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.View
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CoreComponent
import javafx.beans.property.*
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.Pane

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@CoreComponent
class ViewComponent
@JvmOverloads constructor(initialView: View = EmptyView): Component() {

    val parent = Group()

    val z: ReadOnlyIntegerProperty = ReadOnlyIntegerWrapper(0)

    private val propView: ObjectProperty<View> = SimpleObjectProperty<View>(initialView)

    var view: View
        get() = propView.value
        set(value) {
            parent.children.setAll(value.node)
            propView.value = value
        }

    override fun onAdded() {
        parent.translateXProperty().bind(entity.xProperty().subtract(entity.transformComponent.positionOriginXProperty()))
        parent.translateYProperty().bind(entity.yProperty().subtract(entity.transformComponent.positionOriginYProperty()))

        (z as ReadOnlyIntegerWrapper).bind(entity.transformComponent.zProperty())

        // TODO: bind rotation and origin
    }

    override fun onUpdate(tpf: Double) {
        view.onUpdate(tpf)
    }

    override fun onRemoved() {
        view.dispose()
    }
    //fun addClickListener
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