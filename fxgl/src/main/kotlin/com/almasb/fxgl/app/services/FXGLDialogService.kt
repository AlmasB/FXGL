/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.ui.*
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.effect.DropShadow
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLDialogService : DialogService() {

    private lateinit var uiFactory: UIFactoryService
    private lateinit var dialogFactory: DialogFactoryService
    private lateinit var sceneService: SceneService

    private lateinit var window: MDIWindow

    private lateinit var dialogScene: SubScene

    private val states = ArrayDeque<DialogData>()

    override fun onInit() {
        window = uiFactory.newWindow()

        window.isManuallyResizable = false
        window.isMovable = false
        window.isMinimizable = false
        window.isCloseable = false

        window.layoutXProperty().bind(window.widthProperty().divide(2).negate().add(sceneService.prefWidthProperty().divide(2)))
        window.layoutYProperty().bind(window.heightProperty().divide(2).negate().add(sceneService.prefHeightProperty().divide(2)))

        window.effect = DropShadow(25.0, Color.BLACK)

        dialogScene = DialogSubScene(window, sceneService.prefWidthProperty(), sceneService.prefHeightProperty())

        // keep traversal input within this node
        initTraversalPolicy()
    }

    private fun initTraversalPolicy() {
        window.addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.TAB
                    || it.code == KeyCode.UP
                    || it.code == KeyCode.DOWN
                    || it.code == KeyCode.LEFT
                    || it.code == KeyCode.RIGHT) {
                it.consume()
            }
        }
    }

    val isShowing: Boolean
        get() = dialogScene.contentRoot.scene != null

    /**
     * Replaces all content of the internal window by given node.
     * Creates an appropriate size rectangle box around the node
     * to serve as background.
     *
     * @param title window title
     * @param content content pane
     */
    private fun show(title: String, content: Pane) {
        if (isShowing) {
            // dialog was requested while being shown so remember state
            states.push(DialogData(window.title, window.contentPane))
        }

        window.title = title
        window.contentPane = content

        show()
    }

    private fun show() {
        if (!isShowing) {
            sceneService.pushSubScene(dialogScene)

            dialogScene.contentRoot.requestFocus()
        }
    }

    private fun close() {
        if (states.isEmpty()) {
            sceneService.popSubScene()
        } else {
            val data = states.pop()
            window.title = data.title
            window.contentPane = data.contentPane
        }
    }

    override fun showMessageBox(message: String) {
        showMessageBox(message, EmptyRunnable)
    }

    override fun showMessageBox(message: String, callback: Runnable) {
        val dialog = dialogFactory.messageDialog(message) {
            close()
            callback.run()
        }

        show("Message", dialog)
    }

    override fun showConfirmationBox(message: String, resultCallback: Consumer<Boolean>) {
        val dialog = dialogFactory.confirmationDialog(message) {
            close()
            resultCallback.accept(it)
        }

        show("Confirm", dialog)
    }

    override fun <T : Any> showChoiceBox(message: String, callback: Consumer<T>, firstOption: T, vararg options: T) {
        val dialog = dialogFactory.choiceDialog(message, { result ->
            close()
            callback.accept(result)
        }, firstOption, *options)

        show("Choice", dialog)
    }

    override fun showInputBox(message: String, resultCallback: Consumer<String>) {
        showInputBox(message, Predicate { true }, resultCallback)
    }

    override fun showInputBox(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        val dialog = dialogFactory.inputDialog(message, filter, Consumer {
            close()
            resultCallback.accept(it)
        })

        show("Input", dialog)
    }

    override fun showInputBoxWithCancel(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        val dialog = dialogFactory.inputDialogWithCancel(message, filter, Consumer {
            close()
            resultCallback.accept(it)
        })

        show("Input", dialog)
    }

    override fun showErrorBox(error: Throwable) {
        showErrorBox(error, EmptyRunnable)
    }

    private fun showErrorBox(error: Throwable, callback: Runnable) {
        val dialog = dialogFactory.errorDialog(error) {
            close()
            callback.run()
        }

        show("Error", dialog)
    }

    override fun showErrorBox(errorMessage: String, callback: Runnable) {
        val dialog = dialogFactory.errorDialog(errorMessage) {
            close()
            callback.run()
        }

        show("Error", dialog)
    }

    override fun showBox(message: String, content: Node, vararg buttons: Button) {
        val dialog = dialogFactory.customDialog(message, content, Runnable { close() }, *buttons)

        show("Dialog", dialog)
    }

    override fun showProgressBox(message: String): DialogBox {
        val dialog = dialogFactory.progressDialogIndeterminate(message) {
            close()
        }

        show("Progress", dialog)

        return object : DialogBox {
            override fun close() {
                this@FXGLDialogService.close()
            }
        }
    }

    override fun showProgressBox(message: String, observable: ReadOnlyDoubleProperty, callback: Runnable) {
        val dialog = dialogFactory.progressDialog(message, observable) {
            close()
            callback.run()
        }

        show("Progress", dialog)
    }

    private class DialogData(val title: String, val contentPane: Pane)

    private class DialogSubScene(window: MDIWindow, widthProperty: ReadOnlyDoubleProperty, heightProperty: ReadOnlyDoubleProperty) : SubScene() {
        init {
            contentRoot.prefWidthProperty().bind(widthProperty)
            contentRoot.prefHeightProperty().bind(heightProperty)
            contentRoot.background = Background(BackgroundFill(Color.rgb(127, 127, 123, 0.5), null, null))

            contentRoot.children.addAll(window)
        }
    }
}