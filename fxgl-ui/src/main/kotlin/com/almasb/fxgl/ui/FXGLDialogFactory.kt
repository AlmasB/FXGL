/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.core.util.Predicate
import javafx.beans.property.DoubleProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableStringValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLDialogFactory : DialogFactory {

    private fun createMessage(message: String): Text {
        return FXGLUIConfig.getUIFactory().newText(message)
    }

    /**
     * Creates a rectangular wrapper around the content node.
     */
    private fun wrap(n: Node): Pane {
        val wrapper = StackPane(n)
        wrapper.minWidth = 600.0
        wrapper.padding = Insets(20.0)
        wrapper.styleClass.add("dialog-border")

        return wrapper
    }

    override fun messageDialog(message: String): Pane {
        return messageDialog(message, EmptyRunnable)
    }

    override fun messageDialog(message: String, callback: Runnable): Pane {
        val text = createMessage(message)

        val btnOK = FXGLUIConfig.getUIFactory().newButton("")
        btnOK.textProperty().bind(localizedStringProperty("dialog.ok"))
        btnOK.setOnAction {
            callback.run()
        }

        val vbox = VBox(50.0, text, btnOK)
        vbox.setAlignment(Pos.CENTER)

        return wrap(vbox)
    }

    override fun confirmationDialog(message: String, callback: Consumer<Boolean>): Pane {
        val text = createMessage(message)

        val btnYes = FXGLButton()
        btnYes.textProperty().bind(localizedStringProperty("dialog.yes"))
        btnYes.setOnAction {
            callback.accept(true)
        }

        val btnNo = FXGLButton()
        btnNo.textProperty().bind(localizedStringProperty("dialog.no"))
        btnNo.setOnAction {
            callback.accept(false)
        }

        val hbox = HBox(btnYes, btnNo)
        hbox.alignment = Pos.CENTER

        val vbox = VBox(50.0, text, hbox)
        vbox.setAlignment(Pos.CENTER)

        return wrap(vbox)
    }

    override fun inputDialog(message: String, callback: Consumer<String>): Pane {
        return inputDialog(message, Predicate { true }, callback)
    }

    override fun inputDialog(message: String, filter: Predicate<String>, callback: Consumer<String>): Pane {
        val text = createMessage(message)

        val field = TextField()
        field.maxWidth = Math.max(text.layoutBounds.width, 200.0)
        field.font = FXGLUIConfig.getUIFactory().newFont(18.0)

        val btnOK = FXGLButton("")
        btnOK.textProperty().bind(localizedStringProperty("dialog.ok"))

        field.textProperty().addListener { _, _, newInput ->
            btnOK.isDisable = newInput.isEmpty() || !filter.test(newInput)
        }

        btnOK.isDisable = true
        btnOK.setOnAction {
            callback.accept(field.text)
        }

        val vbox = VBox(50.0, text, field, btnOK)
        vbox.setAlignment(Pos.CENTER)

        return wrap(vbox)
    }

    override fun inputDialogWithCancel(message: String, filter: Predicate<String>, callback: Consumer<String>): Pane {
        val text = createMessage(message)

        val field = TextField()
        field.maxWidth = Math.max(text.layoutBounds.width, 200.0)
        field.font = FXGLUIConfig.getUIFactory().newFont(18.0)

        val btnOK = FXGLUIConfig.getUIFactory().newButton("")
        btnOK.textProperty().bind(localizedStringProperty("dialog.ok"))

        field.textProperty().addListener {
            _, _, newInput -> btnOK.isDisable = newInput.isEmpty() || !filter.test(newInput)
        }

        btnOK.isDisable = true
        btnOK.setOnAction {
            callback.accept(field.text)
        }

        val btnCancel = FXGLUIConfig.getUIFactory().newButton("")
        btnCancel.textProperty().bind(localizedStringProperty("dialog.cancel"))
        btnCancel.setOnAction {
            callback.accept("")
        }

        val hBox = HBox(btnOK, btnCancel)
        hBox.alignment = Pos.CENTER

        val vbox = VBox(50.0, text, field, hBox)
        vbox.setAlignment(Pos.CENTER)

        return wrap(vbox)
    }

    override fun errorDialog(error: Throwable): Pane {
        return errorDialog(error, EmptyRunnable)
    }

    override fun errorDialog(errorMessage: String): Pane {
        return errorDialog(errorMessage, EmptyRunnable)
    }

    override fun errorDialog(errorMessage: String, callback: Runnable): Pane {
        return messageDialog("Error occurred: $errorMessage", callback)
    }

    override fun errorDialog(error: Throwable, callback: Runnable): Pane {
        val text = createMessage(error.toString())

        val btnOK = FXGLButton("")
        btnOK.textProperty().bind(localizedStringProperty("dialog.ok"))
        btnOK.setOnAction {
            callback.run()
        }

        val btnLog = FXGLButton("LOG")
        btnLog.setOnAction {
//            val sw = StringWriter()
//            val pw = PrintWriter(sw)
//            error.printStackTrace(pw)
//            pw.close()
//
//            try {
//                Files.write(Paths.get("LastException.log"), sw.toString().split("\n".toRegex()).dropLastWhile { it.isEmpty() })
//                DialogSubState.showMessageBox("Log has been saved as LastException.log")
//            } catch (ex: Exception) {
//                DialogSubState.showMessageBox("Failed to save log file")
//            }

            callback.run()
        }

        val hbox = HBox(btnOK, btnLog)
        hbox.alignment = Pos.CENTER

        val vbox = VBox(50.0, text, hbox)
        vbox.setAlignment(Pos.CENTER)

        return wrap(vbox)
    }

    override fun progressDialog(message: String, observable: DoubleProperty, callback: Runnable): Pane {
        val progress = ProgressBar()
        progress.setPrefSize(200.0, 50.0)
        progress.progressProperty().bind(observable)

        var listener: ChangeListener<Number>? = null

        listener = ChangeListener { _, _, value ->
            if (value.toDouble() >= 1) {
                progress.progressProperty().unbind()
                progress.progressProperty().removeListener(listener)
                listener = null
                callback.run()
            }
        }

        progress.progressProperty().addListener(listener)

        val text = createMessage(message)

        val vbox = VBox(50.0, text, progress)
        vbox.alignment = Pos.CENTER

        return wrap(vbox)
    }

    override fun progressDialogIndeterminate(message: String, callback: Runnable): Pane {
        val progress = ProgressIndicator()
        progress.setPrefSize(200.0, 200.0)

        val btn = Button()
        btn.isVisible = false

        return customDialog(message, progress, callback, btn)
    }

    override fun customDialog(message: String, content: Node, callback: Runnable, vararg buttons: Button): Pane {
        for (btn in buttons) {
            val handler = btn.onAction

            btn.setOnAction { e ->
                callback.run()

                handler?.handle(e)
            }
        }

        val text = createMessage(message)

        val hbox = HBox(*buttons)
        hbox.alignment = Pos.CENTER

        val vbox = VBox(50.0, text, content, hbox)
        vbox.setAlignment(Pos.CENTER)

        return wrap(vbox)
    }

    private fun localizedStringProperty(key: String): ObservableStringValue {
        return FXGLUIConfig.getLocalizationService().localizedStringProperty(key)
    }
}