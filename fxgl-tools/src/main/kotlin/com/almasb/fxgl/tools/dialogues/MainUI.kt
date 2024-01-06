/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.SerializableGraph
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.tools.dialogues.ui.ErrorIcon
import com.almasb.fxgl.tools.dialogues.ui.FXGLContextMenu
import com.almasb.fxgl.tools.dialogues.ui.NodeInspectorPane
import com.almasb.fxgl.ui.InGamePanel
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.stage.FileChooser
import javafx.util.Duration
import java.io.File
import java.nio.file.Files

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MainUI : BorderPane() {

    private val toolbar = HBox(35.0)
    private val tabPane = TabPane()

    private val nodeInspectorPane = NodeInspectorPane()

    private val errorIcon = ErrorIcon()

    private val preferences by lazy { PreferencesSubScene() }

    private val currentTab: DialogueTab?
        get() = tabPane.selectionModel.selectedItem as? DialogueTab

    init {
        toolbar.prefWidthProperty().bind(
                Bindings.`when`(Bindings.isNotEmpty(tabPane.tabs)).then(FXGL.getSettings().actualWidth.div(2.0)).otherwise(FXGL.getSettings().actualWidth)
        )
        toolbar.prefHeight = 30.0
        toolbar.style = "-fx-background-color: black"
        toolbar.alignment = Pos.CENTER_LEFT

        errorIcon.layoutXProperty().bind(FXGL.getSettings().actualWidthProperty().subtract(35))
        errorIcon.layoutY = 40.0

        val tooltip = Tooltip()
        tooltip.showDelay = Duration.ZERO;
        tooltip.text = "There are incomplete dialogue paths."

        Tooltip.install(errorIcon, tooltip)

        tabPane.selectionModel.selectedItemProperty().addListener { _, _, newTab ->
            newTab?.let {
                errorIcon.visibleProperty().unbind()
                errorIcon.visibleProperty().bind((newTab as DialogueTab).pane.isConnectedProperty.not())
            }
        }

        val mdiWindow = getUIFactoryService().newWindow()
        mdiWindow.title = "Node Inspector"
        mdiWindow.isMinimizable = false
        mdiWindow.isCloseable = false
        mdiWindow.isMovable = false
        // TODO: allow resize only in a single direction
        mdiWindow.isManuallyResizable = false
        mdiWindow.setPrefSize(300.0, 300.0)
        mdiWindow.relocate(0.0, 30.0)
        mdiWindow.background = Background(BackgroundFill(Color.color(0.0, 0.0, 0.0, 0.5), null, null))
        mdiWindow.contentPane = nodeInspectorPane
        mdiWindow.prefHeightProperty().bind(getSettings().actualHeightProperty().subtract(30))

        nodeInspectorPane.prefWidthProperty().bind(mdiWindow.prefWidthProperty())
        nodeInspectorPane.prefHeightProperty().bind(mdiWindow.prefHeightProperty().subtract(28))

        val contextMenuFile = FXGLContextMenu()
        contextMenuFile.addItem("New (CTRL+N)") { openNewDialog() }
        contextMenuFile.addItem("Open... (CTRL+O)") { openLoadDialog() }
        contextMenuFile.addItem("Save (CTRL+S)") { currentTab?.let { onSave(it) } }
        contextMenuFile.addItem("Save As...") { currentTab?.let { openSaveAsDialog(it) } }
        contextMenuFile.addItem("Save All") { onSaveAll() }
        contextMenuFile.addItem("Exit") { getGameController().exit() }

        val contextMenuEdit = FXGLContextMenu()
        contextMenuEdit.addItem("Undo (CTRL+Z)") { undo() }
        //contextMenuEdit.addItem("Redo") { redo() }
        contextMenuEdit.addItem("Duplicate Node (CTRL+D)") { duplicate() }
        contextMenuEdit.addItem("Preferences") { openPreferencesDialog() }

        val contextMenuAdd = FXGLContextMenu()
        contextMenuAdd.addItem("Node (CTRL+Left Click)") { currentTab?.pane?.openAddNodeDialog() }

        val contextMenuHelp = FXGLContextMenu()
        contextMenuHelp.addItem("Check for Updates...") { FXGL.getFXApp().hostServices.showDocument("https://fxgl.itch.io/fxgl-dialogue-editor") }
        contextMenuHelp.addItem("About") { openAboutDialog() }

        val pane = Pane(tabPane, toolbar, errorIcon, mdiWindow)

        val menuFile = EditorMenu("File") {
            contextMenuFile.show(pane, 0.0, toolbar.prefHeight)
        }

        val menuEdit = EditorMenu("Edit") {
            contextMenuEdit.show(pane, 70.0, toolbar.prefHeight)
        }

        val menuAdd = EditorMenu("Add") {
            contextMenuAdd.show(pane, 100.0, toolbar.prefHeight)
        }

        val menuHelp = EditorMenu("Help") {
            contextMenuHelp.show(pane, 170.0, toolbar.prefHeight)
        }

        val menuBar = MenuBar(menuFile, menuEdit, menuAdd, menuHelp)
        menuBar.style = "-fx-background-color: black"

        toolbar.children += menuBar
        toolbar.children += makeRunButton()

        pane.style = "-fx-background-color: gray"
        tabPane.prefWidthProperty().bind(FXGL.getSettings().actualWidthProperty())
        tabPane.prefHeightProperty().bind(FXGL.getSettings().actualHeightProperty())

        val exitButton = Button("Exit")
        exitButton.translateXProperty().bind(FXGL.getSettings().actualWidthProperty().subtract(45))
        exitButton.translateY = 15.0
        exitButton.setOnAction { FXGL.getCutsceneService().dialogueScene.endDialogue() }

        FXGL.getCutsceneService().dialogueScene.contentRoot.children += exitButton

        center = pane

        openNewTab(File("Untitled.json"))

        initInput()
    }

    private fun initInput() {
        getInput().addAction(object : UserAction("New") {
            override fun onActionBegin() {
                openNewDialog()
            }
        }, KeyCode.N, InputModifier.CTRL)

        getInput().addAction(object : UserAction("Open") {
            override fun onActionBegin() {
                openLoadDialog()
            }
        }, KeyCode.O, InputModifier.CTRL)

        getInput().addAction(object : UserAction("Save") {
            override fun onActionBegin() {
                currentTab?.let { onSave(it) }
            }
        }, KeyCode.S, InputModifier.CTRL)

        getInput().addAction(object : UserAction("Undo") {
            override fun onActionBegin() {
                undo()
            }
        }, KeyCode.Z, InputModifier.CTRL)

        getInput().addAction(object : UserAction("Duplicate") {
            override fun onActionBegin() {
                duplicate()
            }
        }, KeyCode.D, InputModifier.CTRL)
    }

    private fun makeRunButton(): Node {
        val stack = StackPane()

        val bgRun = Rectangle(18.0, 18.0, null)
        bgRun.strokeProperty().bind(
                Bindings.`when`(stack.hoverProperty()).then(Color.WHITE).otherwise(Color.TRANSPARENT)
        )
        bgRun.fillProperty().bind(
                Bindings.`when`(stack.pressedProperty()).then(Color.color(0.5, 0.5, 0.5, 0.95)).otherwise(Color.TRANSPARENT)
        )

        val btnRun = Polygon(
                0.0, 0.0,
                13.0, 6.5,
                0.0, 13.0
        )
        btnRun.fill = Color.LIGHTGREEN

        stack.cursor = Cursor.HAND

        stack.setOnMouseClicked {
            stack.requestFocus()

            currentTab?.pane?.let {
                FXGL.getCutsceneService().startDialogueScene(it.graph)
            }
        }

        stack.children.addAll(bgRun, btnRun)

        return stack
    }

    private fun openNewTab(file: File): DialogueTab {
        val tab = DialogueTab(file, DialoguePane())
        tab.pane.selectedNodeView.addListener { _, _, newNodeView ->
            nodeInspectorPane.updateSelection(tab.pane.graph, newNodeView.node)
        }

        tabPane.tabs += tab
        tabPane.selectionModel.select(tab)

        return tab
    }

    private fun openPreferencesDialog() {
        FXGL.getSceneService().pushSubScene(preferences)
    }

    private fun openNewDialog() {
        getDialogService().showInputBox("New dialogue name", { isValidName(it) }, { name ->
            openNewTab(File("$name.json"))
        })
    }

    private fun onSave(tab: DialogueTab) {
        val serializedGraph = tab.pane.save()

        val s = mapper.writeValueAsString(serializedGraph)

        Files.writeString(tab.file.toPath(), s)
    }

    private fun onSaveAll() {
        tabPane.tabs
                .map { it as DialogueTab }
                .forEach { onSave(it) }
    }

    private val mapper = ObjectMapper().also { it.enable(SerializationFeature.INDENT_OUTPUT) }

    private fun openSaveAsDialog(tab: DialogueTab) {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.initialFileName = tab.file.name

        chooser.showSaveDialog(scene.window)?.let {

            val file: File =
                    if (it.name.endsWith(".json")) {
                        it
                    } else {
                        File(it.parentFile, it.nameWithoutExtension + ".json")
                    }

            val serializedGraph = tab.pane.save()

            val s = mapper.writeValueAsString(serializedGraph)

            Files.writeString(file.toPath(), s)

            tab.updateFile(file)
        }
    }

    private fun openLoadDialog() {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.extensionFilters += FileChooser.ExtensionFilter("FXGL dialogue files", "*.json")

        chooser.showOpenDialog(scene.window)?.let {
            val tab = openNewTab(it)

            tab.pane.load(mapper.readValue(it, SerializableGraph::class.java))
        }
    }

    private fun undo() {
        currentTab?.pane?.undo()
    }

    private fun redo() {
        currentTab?.pane?.redo()
    }

    private fun duplicate() {
        currentTab?.pane?.duplicate()
    }

    private fun openAboutDialog() {
        showMessage(
                "${getSettings().title}: v.${getSettings().version}\n\n"
                        + "Report issues / chat: https://gitter.im/AlmasB/FXGL\n"
        )
    }

    private fun isValidName(name: String): Boolean {
        return name.all { it.isLetterOrDigit() || it == '_' }
    }

    private class EditorMenu(name: String, action: () -> Unit) : Menu("") {
        init {
            graphic = getUIFactoryService().newText(name).also {
                it.setOnMouseClicked {
                    action()
                }

                it.fillProperty().bind(
                        Bindings.`when`(it.hoverProperty()).then(Color.LIGHTBLUE).otherwise(Color.WHITE)
                )
            }

            style = "-fx-background-color: black"
        }
    }

    private class DialogueTab(var file: File,
                              val pane: DialoguePane) : Tab(file.nameWithoutExtension) {
        init {
            pane.prefWidthProperty().bind(FXGL.getSettings().actualWidthProperty())
            pane.prefHeightProperty().bind(FXGL.getSettings().actualHeightProperty())

            content = pane

            textProperty().bind(
                    Bindings.`when`(pane.isDirtyProperty).then(file.nameWithoutExtension + "*").otherwise(file.nameWithoutExtension)
            )
        }

        fun updateFile(newFile: File) {
            file = newFile

            textProperty().bind(
                    Bindings.`when`(pane.isDirtyProperty).then(file.nameWithoutExtension + "*").otherwise(file.nameWithoutExtension)
            )
        }
    }
}