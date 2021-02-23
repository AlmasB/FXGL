/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.editor

import com.almasb.fxgl.app.FXGLPane
import com.almasb.fxgl.dev.DevPane
import com.almasb.fxgl.dev.editor.EntityInspector
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.dsl.FXGL.Companion.entityBuilder
import com.almasb.fxgl.dsl.FXGL.Companion.getAppHeight
import com.almasb.fxgl.dsl.components.ProjectileComponent
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import com.almasb.fxgl.ui.FXGLButton
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.SplitPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EditorMainUI : BorderPane() {

    private val BG_COLOR = Color.rgb(32, 37, 54)
    private val TOOLBAR_COLOR = BG_COLOR.darker()

    private val centerBox = VBox(10.0)

    private val explorerView = VBox(10.0)

    private val splitPane = SplitPane()

    private val entityInspector = EntityInspector()

    init {
        background = Background(BackgroundFill(BG_COLOR, null, null))

        val toolBar = HBox(5.0)
        toolBar.padding = Insets(0.0, 0.0, 0.0, 10.0)
//        toolbar.prefWidthProperty().bind(
//                Bindings.`when`(Bindings.isNotEmpty(tabPane.tabs)).then(FXGL.getSettings().actualWidth.div(2.0)).otherwise(FXGL.getSettings().actualWidth)
//        )
        toolBar.prefHeight = 30.0
        toolBar.style = "-fx-background-color: rgb(${TOOLBAR_COLOR.red*255}, ${TOOLBAR_COLOR.green*255}, ${TOOLBAR_COLOR.blue*255})"
        toolBar.alignment = Pos.CENTER_LEFT
        toolBar.children.addAll(
                Text("Menu 1").also {
                    it.fill = Color.WHITE

                    it.setOnMouseClicked {
                        var e = entityBuilder()
                                .at(0.0, (getAppHeight() / 2 - 32).toDouble())
                                .view(Rectangle(64.0, 64.0, Color.BLUE))
                                .with(ProjectileComponent(Point2D(1.0, 0.0), 150.0))
                                .buildAndAttach()

                        entityInspector.entity = e
                    }
                },

                Text("Menu 2").also { it.fill = Color.WHITE },
                Text("Menu 3").also { it.fill = Color.WHITE },
                Button("Play").also { it.setOnAction { getGameController().resumeEngine() } },
                Button("Pause").also { it.setOnAction { getGameController().pauseEngine() } },
                Button("Stop").also {
                    it.setOnAction {
                        getGameController().resumeEngine()
                        getGameController().startNewGame()
                        getGameController().pauseEngine()
                    }
                }
        )

        val statusBar = HBox(5.0)
        statusBar.padding = Insets(0.0, 0.0, 0.0, 10.0)
        statusBar.prefHeight = 25.0
        statusBar.style = "-fx-background-color: rgb(${TOOLBAR_COLOR.red*255}, ${TOOLBAR_COLOR.green*255}, ${TOOLBAR_COLOR.blue*255})"
        statusBar.alignment = Pos.CENTER_LEFT
        statusBar.children.addAll(
                Text("Status Bar").also { it.fill = Color.WHITE }
        )


        explorerView.padding = Insets(5.0)
        explorerView.children.addAll(
                Rectangle(300.0, 450.0, Color.LIGHTGRAY),
                Rectangle(300.0, 390.0, Color.LIGHTGRAY)
        )

        val inspectorView = VBox(10.0)
        inspectorView.padding = Insets(5.0)
        inspectorView.children.addAll(Rectangle(455.0, 850.0, Color.LIGHTGRAY))

        val assetView = HBox(10.0)
        assetView.children.addAll(Rectangle(800.0, 240.0, Color.LIGHTGRAY))

        centerBox.padding = Insets(5.0)
        //centerBox.children.addAll(assetView)





        splitPane.items.addAll(centerBox)
        splitPane.background = Background(BackgroundFill(BG_COLOR, null, null))


        splitPane.items += entityInspector

        top = toolBar
        bottom = statusBar
//        left = explorerView
//        right = inspectorView
        center = splitPane
    }

    fun addPane(pane:FXGLPane) {
        centerBox.children.add(0, pane)
    }

    fun notifyDone() {
        getAssetLoader().loadCSS("fxgl_dark.css").also {
            stylesheets.add(it.externalForm)
        }
    }
}