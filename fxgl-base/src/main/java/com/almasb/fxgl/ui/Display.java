/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.core.util.Consumer;
import com.almasb.fxgl.core.util.Predicate;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Provides access to dialogs.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface Display {

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button. On
     * button press, the message box will be dismissed.
     *
     * @param message the message to show
     */
    void showMessageBox(String message);

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button. On
     * button press, the message box will be dismissed and the callback function called.
     *
     * @param message the message to show
     * @param callback the function to be called when dialog is dismissed
     */
    void showMessageBox(String message, Runnable callback);

    /**
     * Shows a blocking message box with YES and NO buttons. The callback is
     * invoked with the user answer as parameter.
     *
     * @param message        message to show
     * @param resultCallback the function to be called
     */
    void showConfirmationBox(String message, Consumer<Boolean> resultCallback);

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * @param resultCallback the function to be called
     */
    void showInputBox(String message, Consumer<String> resultCallback);

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * @param filter  filter to validate input
     * @param resultCallback the function to be called
     */
    void showInputBox(String message, Predicate<String> filter, Consumer<String> resultCallback);

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK and CANCEL buttons and input field.
     * The callback is invoked with the field text as parameter, or with empty string if dialog was cancelled.
     *
     * @param message message to show
     * @param filter the filter to validate input
     * @param resultCallback result function to call back or empty string if use cancelled the dialog
     */
    void showInputBoxWithCancel(String message, Predicate<String> filter, Consumer<String> resultCallback);

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param error the error to show
     */
    void showErrorBox(Throwable error);

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param errorMessage error message to show
     * @param callback the function to be called when dialog is dismissed
     */
    void showErrorBox(String errorMessage, Runnable callback);

    /**
     * Shows a blocking (stops game execution, method returns normally) generic dialog.
     *
     * @param message the message
     * @param content the content
     * @param buttons buttons present
     */
    void showBox(String message, Node content, Button... buttons);

    /**
     * Shows a blocking (stops game execution, method returns normally) progress dialog.
     * Can only be dismissed via the returned handler.
     *
     * @param message message to show
     * @return dialog handler
     */
    DialogBox showProgressBox(String message);

    /**
     * @param message message to show
     * @param progress [0..1]
     * @param callback called when dialog is dismissed (when progress >= 1)
     */
    void showProgressBox(String message, DoubleProperty progress, Runnable callback);
}
