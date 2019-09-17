/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues.ui

import com.almasb.fxgl.dsl.FXGL
import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * Context menu that is rendered inside the same window (unlike the JavaFX version).
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLContextMenu : StackPane() {

    private var container: Parent? = null

    private val contentRoot = VBox()

    private val mousePressHandler = EventHandler<MouseEvent> {
        if (!layoutBounds.contains(sceneToLocal(it.sceneX, it.sceneY))) {
            close()
        }
    }

    init {
        styleClass += "fxgl-context-menu"

        contentRoot.padding = Insets(3.0)

        children.addAll(contentRoot)
    }

    fun addItem(name: String, action: Runnable) {
        addItem(name) { action.run() }
    }

    fun addItem(name: String, action: () -> Unit) {
        contentRoot.children += MenuItem(name) {
            action()
            close()
        }
    }

    fun show(container: Parent, sceneX: Double, sceneY: Double) {
        val p = container.sceneToLocal(sceneX, sceneY)

        translateX = p.x
        translateY = p.y

        if (this.container !== container) {
            close()

            container.scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressHandler)

            if (container is Group)
                container.children += this

            if (container is Pane)
                container.children += this

            this.container = container
        }
    }

    fun close() {
        container?.let {
            if (it is Group)
                it.children -= this
            if (it is Pane)
                it.children -= this

            it.scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, mousePressHandler)
        }

        container = null
    }

    private class MenuItem(name: String, action: () -> Unit) : StackPane() {
        init {
            val bg = Rectangle(120.0, 30.0, Color.TRANSPARENT)
            val text = FXGL.getUIFactory().newText(name)

            bg.fillProperty().bind(
                    Bindings.`when`(hoverProperty()).then(Color.WHITE).otherwise(Color.TRANSPARENT)
            )

            text.fillProperty().bind(
                    Bindings.`when`(hoverProperty()).then(Color.BLACK).otherwise(Color.WHITE)
            )

            background = Background(BackgroundFill(null, null, null))

            alignment = Pos.CENTER_LEFT

            children.addAll(bg, text)

            setOnMouseClicked {
                action()
            }
        }
    }
}