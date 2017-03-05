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

package com.almasb.fxgl.service.impl.display

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.service.Display
import com.almasb.fxgl.ui.FXGLButton
import com.sun.javafx.scene.traversal.Algorithm
import com.sun.javafx.scene.traversal.Direction
import com.sun.javafx.scene.traversal.ParentTraversalEngine
import com.sun.javafx.scene.traversal.TraversalContext
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.effect.BoxBlur
import javafx.scene.effect.Effect
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import jfxtras.scene.control.window.Window
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * In-game dialog pane.
 * The pane fills the whole scene area so that user
 * input does not pass through to the underlying nodes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class DialogPane
internal constructor(private val display: Display) : Pane() {

    private val window = Window()

    private var onShown: Runnable? = null
    private var onClosed: Runnable? = null

    init {
        display.currentSceneProperty().addListener { o, oldScene, newScene ->
            // if we somehow changed scene while the dialog is showing
            if (isShowing) {
                closeInScene(oldScene)
                openInScene(newScene)
            }
        }

        val width = FXGL.getSettings().width.toDouble()
        val height = FXGL.getSettings().height.toDouble()

        setPrefSize(width, height)
        background = Background(BackgroundFill(Color.rgb(127, 127, 123, 0.5), null, null))

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
        this.impl_traversalEngine = ParentTraversalEngine(this, object : Algorithm {
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
        get() = parent != null

    /**
     * Shows a simple message box with OK button.
     * Calls back the given runnable on close.
     *
     *
     * Opening more than 1 dialog box is not allowed.

     * @param message message to show
     * *
     * @param callback function to call when closed
     */
    @JvmOverloads internal fun showMessageBox(message: String, callback: Runnable = Runnable { }) {
        val text = createMessage(message)

        val btnOK = FXGLButton("OK")
        btnOK.setOnAction {
            close()
            callback.run()
        }

        val vbox = VBox(50.0, text, btnOK)
        vbox.setAlignment(Pos.CENTER)
        vbox.setUserData(Point2D(Math.max(text.layoutBounds.width, 200.0), text.layoutBounds.height * 2 + 50))

        setContent("Message", vbox)
        show()
    }

    /**
     * Shows an error box with OK and LOG buttons.
     *
     *
     * Opening more than 1 dialog box is not allowed.
     *
     * @param errorMessage error message to show
     */
    internal fun showErrorBox(errorMessage: String) {
        showErrorBox(RuntimeException(errorMessage))
    }

    /**
     * Shows an error box with OK and LOG buttons.
     *
     *
     * Opening more than 1 dialog box is not allowed.

     * @param errorMessage error message to show
     * *
     * @param callback function to call back when closed
     */
    internal fun showErrorBox(errorMessage: String, callback: Runnable) {
        showErrorBox(RuntimeException(errorMessage), callback)
    }

    /**
     * Shows an error box with OK and LOG buttons.
     *
     *
     * Opening more than 1 dialog box is not allowed.

     * @param error error to show
     * *
     * @param callback function to call when closed
     */
    @JvmOverloads internal fun showErrorBox(error: Throwable, callback: Runnable = Runnable { }) {
        val text = createMessage(error.toString())

        val btnOK = FXGLButton("OK")
        btnOK.setOnAction { e ->
            close()
            callback.run()
        }

        val btnLog = FXGLButton("LOG")
        btnLog.setOnAction { e ->
            close()

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            error.printStackTrace(pw)
            pw.close()

            try {
                Files.write(Paths.get("LastException.log"), Arrays.asList(*sw.toString().split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
                showMessageBox("Log has been saved as LastException.log")
            } catch (ex: Exception) {
                showMessageBox("Failed to save log file")
            }

            callback.run()
        }

        val hbox = HBox(btnOK, btnLog)
        hbox.alignment = Pos.CENTER

        val vbox = VBox(50.0, text, hbox)
        vbox.setAlignment(Pos.CENTER)
        vbox.setUserData(Point2D(Math.max(text.layoutBounds.width, 400.0), text.layoutBounds.height * 2 + 50))

        setContent("Error", vbox)
        show()
    }

    /**
     * Shows confirmation message box with YES and NO buttons.
     *
     *
     * The callback function will be invoked with boolean answer
     * as parameter.
     *
     *
     * Opening more than 1 dialog box is not allowed.

     * @param message message to show
     * *
     * @param resultCallback result function to call back
     */
    internal fun showConfirmationBox(message: String, resultCallback: Consumer<Boolean>) {
        val text = createMessage(message)

        val btnYes = FXGLButton("YES")
        btnYes.setOnAction { e ->
            close()
            resultCallback.accept(true)
        }

        val btnNo = FXGLButton("NO")
        btnNo.setOnAction { e ->
            close()
            resultCallback.accept(false)
        }

        val hbox = HBox(btnYes, btnNo)
        hbox.alignment = Pos.CENTER

        val vbox = VBox(50.0, text, hbox)
        vbox.setAlignment(Pos.CENTER)
        vbox.setUserData(Point2D(Math.max(text.layoutBounds.width, 400.0), text.layoutBounds.height * 2 + 50))

        setContent("Confirmation", vbox)
        show()
    }

    /**
     * Shows input box with input field and OK button.
     * The button will stay disabled until there is at least
     * 1 character in the input field.
     *
     *
     * The callback function will be invoked with input field text
     * as parameter.
     *
     *
     * Opening more than 1 dialog box is not allowed.

     * @param message message to show
     * *
     * @param resultCallback result function to call back
     */
    internal fun showInputBox(message: String, resultCallback: Consumer<String>) {
        showInputBox(message, Predicate { true }, resultCallback)
    }

    /**
     * Shows input box with input field and OK button.
     * The button will stay disabled until the input passes given filter.
     *
     *
     * The callback function will be invoked with input field text
     * as parameter.
     *
     *
     * Opening more than 1 dialog box is not allowed.

     * @param message message to show
     * *
     * @param filter the filter to validate input
     * *
     * @param resultCallback result function to call back
     */
    internal fun showInputBox(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        val text = createMessage(message)

        val field = TextField()
        field.maxWidth = Math.max(text.layoutBounds.width, 200.0)
        field.font = FXGL.getUIFactory().newFont(18.0)

        val btnOK = FXGLButton("OK")

        field.textProperty().addListener { observable, oldValue, newInput -> btnOK.isDisable = newInput.isEmpty() || !filter.test(newInput) }

        btnOK.isDisable = true
        btnOK.setOnAction { e ->
            close()
            resultCallback.accept(field.text)
        }

        val vbox = VBox(50.0, text, field, btnOK)
        vbox.setAlignment(Pos.CENTER)
        vbox.setUserData(Point2D(Math.max(text.layoutBounds.width, 200.0), text.layoutBounds.height * 3 + 50 * 2))

        setContent("Input", vbox)
        show()
    }

    /**
     * Shows input box with input field and OK button.
     * The button will stay disabled until the input passes given filter.
     *
     *
     * The callback function will be invoked with input field text
     * as parameter.
     *
     *
     * Opening more than 1 dialog box is not allowed.

     * @param message message to show
     * *
     * @param filter the filter to validate input
     * *
     * @param resultCallback result function to call back or empty string if use cancelled the dialog
     */
    internal fun showInputBoxWithCancel(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        val text = createMessage(message)

        val field = TextField()
        field.maxWidth = Math.max(text.layoutBounds.width, 200.0)
        field.font = FXGL.getUIFactory().newFont(18.0)

        val btnOK = FXGL.getUIFactory().newButton("OK")

        field.textProperty().addListener { observable, oldValue, newInput -> btnOK.isDisable = newInput.isEmpty() || !filter.test(newInput) }

        btnOK.isDisable = true
        btnOK.setOnAction { e ->
            close()
            resultCallback.accept(field.text)
        }

        val btnCancel = FXGL.getUIFactory().newButton("CANCEL")
        btnCancel.setOnAction { e ->
            close()
            resultCallback.accept("")
        }

        val hBox = HBox(btnOK, btnCancel)
        hBox.alignment = Pos.CENTER

        val vbox = VBox(50.0, text, field, hBox)
        vbox.setAlignment(Pos.CENTER)
        vbox.setUserData(Point2D(Math.max(text.layoutBounds.width, 200.0), text.layoutBounds.height * 3 + 50 * 2))

        setContent("Input", vbox)
        show()
    }

    /**
     * Shows arbitrary box with message, content and given buttons.

     * @param message the header message
     * *
     * @param content the box content
     * *
     * @param buttons buttons present in the box
     */
    internal fun showBox(message: String, content: Node, vararg buttons: Button) {
        for (btn in buttons) {
            val handler = btn.onAction

            btn.setOnAction { e ->
                close()

                handler?.handle(e)
            }
        }

        val text = createMessage(message)

        val hbox = HBox(*buttons)
        hbox.alignment = Pos.CENTER

        val vbox = VBox(50.0, text, content, hbox)
        vbox.setAlignment(Pos.CENTER)
        vbox.setUserData(Point2D(Math.max(text.layoutBounds.width, 200.0),
                text.layoutBounds.height * 3 + (50 * 2).toDouble() + content.layoutBounds.height))

        setContent("Dialog", vbox)
        show()
    }

    private fun createMessage(message: String): Text {
        return FXGL.getUIFactory().newText(message)
    }

    /**
     * Replaces all content of the scene root by given node.
     * Creates an appropriate size rectangle box around the node
     * to serve as background.

     * @param title window title
     * *
     * @param n content node
     */
    private fun setContent(title: String, n: Node) {
        if (isShowing) {
            // dialog was requested while being shown so remember state
            states.push(DialogData(window.title, window.contentPane))
        }

        val size = n.userData as Point2D

        val box = Rectangle()
        box.widthProperty().bind(Bindings.max(size.x + 200, window.widthProperty()))
        box.height = size.y + 100
        box.translateY = 3.0
        box.stroke = Color.AZURE

        val root = StackPane()
        root.children.setAll(box, n)

        window.title = title
        window.contentPane = root
    }

    internal fun setOnClosed(onClosed: Runnable) {
        this.onClosed = onClosed
    }

    internal fun setOnShown(onShown: Runnable) {
        this.onShown = onShown
    }

    private val states = ArrayDeque<DialogData>()

    internal fun show() {
        if (!isShowing) {
            openInScene(display.currentScene)

            this.requestFocus()

            if (onShown != null)
                onShown!!.run()
        }
    }

    internal fun close() {
        if (states.isEmpty()) {
            closeInScene(display.currentScene)

            if (onClosed != null)
                onClosed!!.run()
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
        scene.root.children.add(this)
    }

    private fun closeInScene(scene: FXGLScene) {
        scene.root.children.remove(this)
        scene.effect = savedEffect
    }

    private class DialogData internal constructor(internal var title: String, internal var contentPane: Pane)

    companion object {

        @JvmField val ALPHANUM = Predicate<String> { input -> input.matches("^[\\pL\\pN]+$".toRegex()) }
    }
}
/**
 * Shows a simple message box with OK button.
 *
 *
 * Opening more than 1 dialog box is not allowed.

 * @param message message to show
 */
/**
 * Shows an error box with OK and LOG buttons.
 *
 *
 * Opening more than 1 dialog box is not allowed.

 * @param error error to show
 */
