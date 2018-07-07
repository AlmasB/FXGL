/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.ui.DialogBox
import com.almasb.fxgl.util.Consumer
import com.almasb.fxgl.util.EmptyRunnable
import com.almasb.fxgl.util.Predicate
import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.effect.BoxBlur
import javafx.scene.effect.Effect
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object DialogSubState : SubState() {

    private val window = FXGL.getUIFactory().newWindow()
    private val dialogFactory = FXGL.getSettings().dialogFactory

    private val states = ArrayDeque<DialogData>()

    init {
        val width = FXGL.getSettings().width.toDouble()
        val height = FXGL.getSettings().height.toDouble()

        (view as Pane).setPrefSize(width, height)
        (view as Pane).background = Background(BackgroundFill(Color.rgb(127, 127, 123, 0.5), null, null))

        window.canResize = false
        window.canMove = false
        window.canMinimize = false
        window.canClose = false

        window.layoutXProperty().bind(window.widthProperty().divide(2).negate().add(width / 2))
        window.layoutYProperty().bind(window.heightProperty().divide(2).negate().add(height / 2))

        children.add(window)

        // keep traversal input within this node
        initTraversalPolicy()
    }

    private fun initTraversalPolicy() {
        window.addEventFilter(KeyEvent.KEY_PRESSED, {
            if (it.code == KeyCode.TAB
                    || it.code == KeyCode.UP
                    || it.code == KeyCode.DOWN
                    || it.code == KeyCode.LEFT
                    || it.code == KeyCode.RIGHT) {
                it.consume()
            }
        })
    }

    val isShowing: Boolean
        get() = view.scene != null

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
            openInScene(FXGL.getApp().scene)

            view.requestFocus()
        }
    }

    private fun close() {
        if (states.isEmpty()) {
            closeInScene(FXGL.getApp().scene)
        } else {
            val data = states.pop()
            window.title = data.title
            window.contentPane = data.contentPane
        }
    }

    private val bgBlur = BoxBlur(5.0, 5.0, 3)
    private var savedEffect: Effect? = null

    private fun openInScene(scene: FXGLScene) {
        savedEffect = scene.effect
        scene.effect = bgBlur

        FXGL.getApp().stateMachine.pushState(this)
    }

    private fun closeInScene(scene: FXGLScene) {
        scene.effect = savedEffect

        FXGL.getApp().stateMachine.popState()
    }

    internal fun showMessageBox(message: String, callback: Runnable) {
        val dialog = dialogFactory.messageDialog(message, {
            close()
            callback.run()
        })

        show("Message", dialog)
    }

    internal fun showConfirmationBox(message: String, resultCallback: Consumer<Boolean>) {
        val dialog = dialogFactory.confirmationDialog(message, {
            close()
            resultCallback.accept(it)
        })

        show("Confirm", dialog)
    }

    internal fun showInputBox(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        val dialog = dialogFactory.inputDialog(message, filter, Consumer {
            close()
            resultCallback.accept(it)
        })

        show("Input", dialog)
    }

    internal fun showInputBoxWithCancel(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        val dialog = dialogFactory.inputDialogWithCancel(message, filter, Consumer {
            close()
            resultCallback.accept(it)
        })

        show("Input", dialog)
    }

    internal fun showErrorBox(error: Throwable) {
        showErrorBox(error, EmptyRunnable)
    }

    internal fun showErrorBox(error: Throwable, callback: Runnable) {
        val dialog = dialogFactory.errorDialog(error, {
            close()
            callback.run()
        })

        show("Error", dialog)
    }

    internal fun showErrorBox(errorMessage: String, callback: Runnable) {
        val dialog = dialogFactory.errorDialog(errorMessage, {
            close()
            callback.run()
        })

        show("Error", dialog)
    }

    internal fun showBox(message: String, content: Node, vararg buttons: Button) {
        val dialog = dialogFactory.customDialog(message, content, Runnable { close() }, *buttons)

        show("Dialog", dialog)
    }

    internal fun showProgressBox(message: String): DialogBox {
        val dialog = dialogFactory.progressDialogIndeterminate(message, {
            close()
        })

        show("Progress", dialog)

        return object : DialogBox {
            override fun close() {
                this@DialogSubState.close()
            }
        }
    }

    internal fun showProgressBox(message: String, observable: DoubleProperty, callback: Runnable) {
        val dialog = dialogFactory.progressDialog(message, observable, {
            close()
            callback.run()
        })

        show("Progress", dialog)
    }

    private class DialogData internal constructor(internal var title: String, internal var contentPane: Pane)
}