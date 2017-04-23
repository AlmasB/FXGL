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

package com.almasb.fxgl.service;

import com.almasb.fxgl.io.UIDialogHandler;
import com.almasb.fxgl.scene.FXGLScene;
import com.almasb.fxgl.saving.UserProfileSavable;
import com.almasb.fxgl.settings.SceneDimension;
import com.almasb.fxgl.util.EmptyRunnable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Display service.
 * Provides access to dialogs and display settings.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface Display extends UserProfileSavable {

    /**
     * Must be called on FX thread.
     * Will be called automatically by FXGL application.
     */
    void initAndShow();

    /**
     * Register an FXGL scene to be managed by display settings.
     *
     * @param scene the scene
     */
    void registerScene(FXGLScene scene);

    /**
     * Set current FXGL scene. The scene will be immediately displayed.
     *
     * @param scene the scene
     */
    void setScene(FXGLScene scene);

    /**
     * @return current scene property
     */
    ReadOnlyObjectProperty<FXGLScene> currentSceneProperty();

    /**
     * @return current FXGL scene
     */
    default FXGLScene getCurrentScene() {
        return currentSceneProperty().get();
    }

    /**
     * Saves a screenshot of the current scene into a ".png" file.
     *
     * @return true if the screenshot was saved successfully, false otherwise
     */
    boolean saveScreenshot();

    /**
     * @return a list of supported scene dimensions with 360, 480, 720 and 1080 heights
     */
    List<SceneDimension> getSceneDimensions();

    /**
     * Set new scene dimension. This will change the video output
     * resolution and adapt all subsystems.
     *
     * @param dimension scene dimension
     */
    void setSceneDimension(SceneDimension dimension);

    /**
     * @return scale ratio of the current size vs target size
     */
    double getScaleRatio();

    /* DIALOG ACCESS */

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button. On
     * button press, the message box will be dismissed.
     *
     * @param message the message to show
     */
    default void showMessageBox(String message) {
        showMessageBox(message, EmptyRunnable.INSTANCE);
    }

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
    default void showInputBox(String message, Consumer<String> resultCallback) {
        showInputBox(message, s -> true, resultCallback);
    }

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
    UIDialogHandler showProgressBox(String message);
}
