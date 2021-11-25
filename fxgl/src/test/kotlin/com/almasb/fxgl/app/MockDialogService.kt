/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.ui.DialogBox
import com.almasb.fxgl.ui.DialogService
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.Node
import javafx.scene.control.Button
import java.util.function.Consumer
import java.util.function.Predicate

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object MockDialogService : DialogService() {
    override fun showErrorBox(error: Throwable?) {
    }

    override fun showErrorBox(errorMessage: String?, callback: Runnable?) {
    }

    override fun showConfirmationBox(message: String?, resultCallback: Consumer<Boolean>?) {
    }

    override fun <T : Any?> showChoiceBox(message: String?, resultCallback: Consumer<T>?, firstOption: T, vararg options: T) {
    }

    override fun showInputBoxWithCancel(message: String?, filter: Predicate<String>?, resultCallback: Consumer<String>?) {
    }

    override fun showBox(message: String?, content: Node?, vararg buttons: Button?) {
    }

    override fun showMessageBox(message: String?) {
    }

    override fun showMessageBox(message: String?, callback: Runnable?) {
    }

    override fun showProgressBox(message: String?): DialogBox {
        return object : DialogBox {
            override fun close() {
            }
        }
    }

    override fun showProgressBox(message: String?, progress: ReadOnlyDoubleProperty?, callback: Runnable?) {
    }

    override fun showInputBox(message: String?, resultCallback: Consumer<String>?) {
    }

    override fun showInputBox(message: String?, filter: Predicate<String>?, resultCallback: Consumer<String>?) {
    }
}