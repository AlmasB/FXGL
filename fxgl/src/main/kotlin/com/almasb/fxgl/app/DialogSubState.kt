/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.io.UIDialogHandler
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.service.ServiceType
import com.almasb.fxgl.util.EmptyRunnable
import com.sun.javafx.scene.traversal.Algorithm
import com.sun.javafx.scene.traversal.Direction
import com.sun.javafx.scene.traversal.ParentTraversalEngine
import com.sun.javafx.scene.traversal.TraversalContext
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.effect.BoxBlur
import javafx.scene.effect.Effect
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import jfxtras.scene.control.window.Window
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object DialogSubState : SubState() {

    private val window = Window()
    private val dialogFactory = FXGL.getService(ServiceType.DIALOG_FACTORY)

    private val states = ArrayDeque<DialogData>()

    init {
        val width = FXGL.getSettings().width.toDouble()
        val height = FXGL.getSettings().height.toDouble()

        (view as Pane).setPrefSize(width, height)
        (view as Pane).background = Background(BackgroundFill(Color.rgb(127, 127, 123, 0.5), null, null))

        window.isResizableWindow = false
        window.isMovable = false
        window.background = Background(BackgroundFill(Color.BLACK, null, null))

        window.layoutXProperty().bind(window.widthProperty().divide(2).negate().add(width / 2))
        window.layoutYProperty().bind(window.heightProperty().divide(2).negate().add(height / 2))

        children.add(window)

        // this is a workaround to avoid users traversing "through" the dialog to underlying nodes
        initTraversalPolicy()
    }

    @Suppress("DEPRECATION")
    private fun initTraversalPolicy() {
        (view as Pane).impl_traversalEngine = ParentTraversalEngine(view as Pane, object : Algorithm {
            override fun select(owner: Node, dir: Direction, context: TraversalContext): Node {
                return window
            }

            override fun selectFirst(context: TraversalContext): Node {
                return window
            }

            override fun selectLast(context: TraversalContext): Node {
                return window
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
            openInScene(FXGL.getDisplay().currentScene)

            view.requestFocus()
        }
    }

    private fun close() {
        if (states.isEmpty()) {
            closeInScene(FXGL.getDisplay().currentScene)
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

    internal fun showProgressBox(message: String): UIDialogHandler {
        val dialog = dialogFactory.progressDialogIndeterminate(message, {
            close()
        })

        show("Progress", dialog)

        return object : UIDialogHandler {
            override fun show() {
                // no-op
            }

            override fun dismiss() {
                close()
            }
        }
    }

    private class DialogData internal constructor(internal var title: String, internal var contentPane: Pane)
}