/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service;

import com.almasb.fxgl.util.EmptyRunnable;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface DialogFactory {

    Pane messageDialog(String message, Runnable callback);

    default Pane messageDialog(String message) {
        return messageDialog(message, EmptyRunnable.INSTANCE);
    }

    Pane confirmationDialog(String message, Consumer<Boolean> callback);

    default Pane inputDialog(String message, Consumer<String> callback) {
        return inputDialog(message, s -> true, callback);
    }

    Pane inputDialog(String message, Predicate<String> filter, Consumer<String> callback);

    Pane inputDialogWithCancel(String message, Predicate<String> filter, Consumer<String> callback);

    Pane errorDialog(Throwable error, Runnable callback);

    default Pane errorDialog(Throwable error) {
        return errorDialog(error, EmptyRunnable.INSTANCE);
    }

    default Pane errorDialog(String errorMessage) {
        return errorDialog(errorMessage, EmptyRunnable.INSTANCE);
    }

    default Pane errorDialog(String errorMessage, Runnable callback) {
        return messageDialog("Error occurred: " + errorMessage, callback);
    }

    Pane progressDialog(DoubleProperty observable, Runnable callback);

    Pane progressDialogIndeterminate(String message, Runnable callback);

    Pane customDialog(String message, Node content, Runnable callback, Button... buttons);
}
