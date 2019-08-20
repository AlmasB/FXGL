/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues.ui

import com.almasb.fxgl.dsl.FXGL
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Rectangle

/**
 * Context menu that is rendered inside the same window (unlike the JavaFX version).
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLContextMenu : StackPane() {

    private var parent: Group? = null

    private val contentRoot = VBox()

    init {
        styleClass += "fxgl-context-menu"

        contentRoot.padding = Insets(3.0)

//        val bg = Rectangle()
//        bg.widthProperty().bind(contentRoot.widthProperty().add(7.0))
//        bg.heightProperty().bind(contentRoot.heightProperty().add(7.0))
//        bg.arcWidth = 15.0
//        bg.arcHeight = 10.0
//        bg.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
//                Stop(0.0, Color.color(0.0, 0.0, 0.0, 0.25)),
//                Stop(1.0, Color.color(0.0, 0.0, 0.0, 0.75))
//        )
//        bg.stroke = Color.BLACK
//        bg.strokeWidth = 3.0

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

    // TODO: Group / Pane?
    fun show(parent: Group, x: Double, y: Double) {
        translateX = x
        translateY = y

        if (this.parent !== parent) {
            close()

            parent.children += this
            this.parent = parent
        }
    }

    fun close() {
        parent?.children?.remove(this)
        parent = null
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