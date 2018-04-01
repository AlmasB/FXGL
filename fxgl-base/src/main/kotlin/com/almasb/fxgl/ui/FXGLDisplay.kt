/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.app.DialogSubState
import com.almasb.fxgl.util.Consumer
import com.almasb.fxgl.util.EmptyRunnable
import com.almasb.fxgl.util.Predicate
import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.control.Button

/**
 * Display service.
 * Provides access to dialogs and display settings.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLDisplay : Display {

    private val dialogState = DialogSubState

    override fun showMessageBox(message: String) {
        showMessageBox(message, EmptyRunnable)
    }

    override fun showMessageBox(message: String, callback: Runnable) {
        dialogState.showMessageBox(message, callback)
    }

    /**
     * Shows a blocking message box with YES and NO buttons. The callback is
     * invoked with the user answer as parameter.
     *
     * @param message        message to show
     *
     * @param resultCallback the function to be called
     */
    override fun showConfirmationBox(message: String, resultCallback: Consumer<Boolean>) {
        dialogState.showConfirmationBox(message, resultCallback)
    }

    override fun showInputBox(message: String, resultCallback: Consumer<String>) {
        showInputBox(message, Predicate { true }, resultCallback)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * *
     * @param filter  filter to validate input
     * *
     * @param resultCallback the function to be called
     */
    override fun showInputBox(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        dialogState.showInputBox(message, filter, resultCallback)
    }

    override fun showInputBoxWithCancel(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        dialogState.showInputBoxWithCancel(message, filter, resultCallback)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param error the error to show
     */
    override fun showErrorBox(error: Throwable) {
        dialogState.showErrorBox(error)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param errorMessage error message to show
     *
     * @param callback the function to be called when dialog is dismissed
     */
    override fun showErrorBox(errorMessage: String, callback: Runnable) {
        dialogState.showErrorBox(errorMessage, callback)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) generic dialog.
     *
     * @param message the message
     *
     * @param content the content
     *
     * @param buttons buttons present
     */
    override fun showBox(message: String, content: Node, vararg buttons: Button) {
        dialogState.showBox(message, content, *buttons)
    }

    override fun showProgressBox(message: String): DialogBox {
        return dialogState.showProgressBox(message)
    }

    override fun showProgressBox(message: String, progress: DoubleProperty, callback: Runnable) {
        dialogState.showProgressBox(message, progress, callback)
    }
}