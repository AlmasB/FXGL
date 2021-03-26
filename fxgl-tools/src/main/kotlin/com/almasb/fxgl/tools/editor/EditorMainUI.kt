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
import com.almasb.fxgl.dsl.components.DraggableComponent
import com.almasb.fxgl.dsl.components.ProjectileComponent
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import com.almasb.fxgl.entity.components.IDComponent
import com.almasb.fxgl.ui.FXGLButton
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration
import javafx.scene.control.ListCell
import javafx.scene.control.cell.TextFieldListCell
import javafx.util.StringConverter


/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EditorMainUI : BorderPane() {

    private companion object {
        private var uniqueID = 0
    }

    private val BG_COLOR = Color.rgb(32, 37, 54)
    private val TOOLBAR_COLOR = BG_COLOR.darker()

    private val centerBox = VBox(10.0)

    private val explorerView = VBox(10.0)

    private val splitPane = SplitPane()

    private val entityInspector = EntityInspector()

    private val entitiesListView = ListView<Entity>()
    private var listViewEditIndex = 0

    private lateinit var fxglPane: FXGLPane

    private val editorEntities = arrayListOf<Entity>()

    private var isStartingFromStop = false

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
                },

                Text("Menu 2").also { it.fill = Color.WHITE },
                Text("Menu 3").also { it.fill = Color.WHITE },
                Button("Play").also {
                    it.setOnAction {
                        play()
                    }

                },
                Button("Pause").also {
                    it.setOnAction {
                        pause()
                    }
                },
                Button("Stop").also {
                    it.setOnAction {
                        stop()
                    }
                },

                Button("Add Entity").also {
                    it.setOnAction {
                        getGameWorld().addEntity(Entity().also {
                            it.isReusable = true
                            it.addComponent(IDComponent("Entity", uniqueID++))
                            it.addComponent(DraggableComponent())
                        })
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
                entitiesListView
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

        entitiesListView.selectionModel.selectedItemProperty().addListener { _, _, newEntity ->
            entityInspector.entity = newEntity
        }

        val converter = object : StringConverter<Entity>() {
            override fun toString(`object`: Entity): String {
                val e = `object`

                return e.getComponentOptional(IDComponent::class.java)
                        .map { it.fullID }
                        .orElse(e.toString())
            }

            override fun fromString(string: String): Entity {
                val name = string
                val e = entitiesListView.items[listViewEditIndex]

                e.getComponent(IDComponent::class.java).name = name

                return e
            }
        }

        entitiesListView.setOnEditStart {
            listViewEditIndex = it.index
        }

        entitiesListView.isEditable = true
        entitiesListView.cellFactory = TextFieldListCell.forListView(converter)



        top = toolBar
        bottom = statusBar
        left = explorerView
//        right = inspectorView
        center = HBox(centerBox, entityInspector)
    }


    private fun play() {

        if (isStartingFromStop) {
            // remove editor entities
            editorEntities.clear()
            editorEntities.addAll(getGameWorld().entities)

            getGameWorld().entitiesCopy.forEach { it.removeFromWorld() }

            // add live entities

            editorEntities.forEach {
                getGameWorld().addEntity(it.copy())
            }
        }

        getGameController().resumeEngine()
    }

    private fun pause() {
        isStartingFromStop = false

        getGameController().pauseEngine()
    }

    private fun stop() {
        isStartingFromStop = true

        // remove live entities

        getGameController().resumeEngine()
        getGameController().startNewGame()
        getGameController().pauseEngine()

        // startNewGame clears all listeners, so re-add
        addEntityListener()

        // add editor entities

        editorEntities.forEach {
            getGameWorld().addEntity(it)
        }
    }

    private fun addEntityListener() {
        getGameWorld().addWorldListener(object : EntityWorldListener {
            override fun onEntityAdded(entity: Entity) {
                entitiesListView.items.add(entity)
            }

            override fun onEntityRemoved(entity: Entity) {
                entitiesListView.items.remove(entity)
            }
        })
    }

    fun addPane(pane: FXGLPane) {
        fxglPane = pane
        centerBox.children.add(0, pane)
    }

    fun notifyDone() {
        stop()

        getAssetLoader().loadCSS("fxgl_dark.css").also {
            stylesheets.add(it.externalForm)
        }
    }

    fun onUpdate(tpf: Double) {

        // the game world does not run (because the engine isn't running), so
        // we run our own DraggableComponent update to be able to move entities...
        getGameWorld().entities.forEach {
            it.getComponentOptional(DraggableComponent::class.java).ifPresent {
                it.onUpdate(tpf)
            }
        }
    }
}


// scratch pad


//internal class EntityCell : ListCell<Entity?>() {
//
//    override fun updateItem(item: Entity?, empty: Boolean) {
//        super.updateItem(item, empty)
//
//        println("update item")
//
//        if (empty || item == null) {
//            text = null
//            graphic = null
//        } else {
//            text = item.getComponentOptional(IDComponent::class.java)
//                    .map { it.fullID }
//                    .orElse(item.toString())
//
//            //graphic = rect
//        }
//    }
//}