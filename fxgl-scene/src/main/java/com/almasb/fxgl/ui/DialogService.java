/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.core.EngineService;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Provides access to dialogs.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class DialogService extends EngineService {

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button. On
     * button press, the message box will be dismissed.
     *
     * @param message the message to show
     */
    public abstract void showMessageBox(String message);

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button. On
     * button press, the message box will be dismissed and the callback function called.
     *
     * @param message the message to show
     * @param callback the function to be called when dialog is dismissed
     */
    public abstract void showMessageBox(String message, Runnable callback);

    /**
     * Shows a blocking message box with YES and NO buttons. The callback is
     * invoked with the user answer as parameter.
     *
     * @param message        message to show
     * @param resultCallback the function to be called
     */
    public abstract void showConfirmationBox(String message, Consumer<Boolean> resultCallback);

    /**
     * Shows a blocking message box with given choices.
     * The callback is invoked with the user answer as parameter.
     *
     * @param message message to show
     * @param resultCallback the function to be called
     * @param firstOption the first option
     * @param options any other options
     * @param <T> type of options
     */
    public abstract <T> void showChoiceBox(String message, Consumer<T> resultCallback, T firstOption, T... options);

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * @param resultCallback the function to be called
     */
    public abstract void showInputBox(String message, Consumer<String> resultCallback);

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * @param filter  filter to validate input
     * @param resultCallback the function to be called
     */
    public abstract void showInputBox(String message, Predicate<String> filter, Consumer<String> resultCallback);

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK and CANCEL buttons and input field.
     * The callback is invoked with the field text as parameter, or with empty string if dialog was cancelled.
     *
     * @param message message to show
     * @param filter the filter to validate input
     * @param resultCallback result function to call back or empty string if use cancelled the dialog
     */
    public abstract void showInputBoxWithCancel(String message, Predicate<String> filter, Consumer<String> resultCallback);

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param error the error to show
     */
    public abstract void showErrorBox(Throwable error);

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param errorMessage error message to show
     * @param callback the function to be called when dialog is dismissed
     */
    public abstract void showErrorBox(String errorMessage, Runnable callback);

    /**
     * Shows a blocking (stops game execution, method returns normally) generic dialog.
     *
     * @param message the message
     * @param content the content
     * @param buttons buttons present
     */
    public abstract void showBox(String message, Node content, Button... buttons);

    /**
     * Shows a blocking (stops game execution, method returns normally) progress dialog.
     * Can only be dismissed via the returned handler.
     *
     * @param message message to show
     * @return dialog handler
     */
    public abstract DialogBox showProgressBox(String message);

    /**
     * @param message message to show
     * @param progress [0..1]
     * @param callback called when dialog is dismissed (when progress >= 1)
     */
    public abstract void showProgressBox(String message, ReadOnlyDoubleProperty progress, Runnable callback);
}
