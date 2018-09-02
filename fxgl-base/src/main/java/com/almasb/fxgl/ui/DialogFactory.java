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
import javafx.scene.layout.Pane;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface DialogFactory {

    Pane messageDialog(String message, Runnable callback);

    Pane messageDialog(String message);

    Pane confirmationDialog(String message, Consumer<Boolean> callback);

    Pane inputDialog(String message, Consumer<String> callback);

    Pane inputDialog(String message, Predicate<String> filter, Consumer<String> callback);

    Pane inputDialogWithCancel(String message, Predicate<String> filter, Consumer<String> callback);

    Pane errorDialog(Throwable error, Runnable callback);

    Pane errorDialog(Throwable error);

    Pane errorDialog(String errorMessage);

    Pane errorDialog(String errorMessage, Runnable callback);

    Pane progressDialog(String message, DoubleProperty observable, Runnable callback);

    Pane progressDialogIndeterminate(String message, Runnable callback);

    Pane customDialog(String message, Node content, Runnable callback, Button... buttons);
}
