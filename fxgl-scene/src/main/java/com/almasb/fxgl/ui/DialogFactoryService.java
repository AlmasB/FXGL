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
import javafx.scene.layout.Pane;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class DialogFactoryService extends EngineService {

    public abstract Pane messageDialog(String message, Runnable callback);

    public abstract Pane messageDialog(String message);

    public abstract Pane confirmationDialog(String message, Consumer<Boolean> callback);

    public abstract <T> Pane choiceDialog(String message, Consumer<T> resultCallback, T firstOption, T... options);

    public abstract Pane inputDialog(String message, Consumer<String> callback);

    public abstract Pane inputDialog(String message, Predicate<String> filter, Consumer<String> callback);

    public abstract Pane inputDialogWithCancel(String message, Predicate<String> filter, Consumer<String> callback);

    public abstract Pane errorDialog(Throwable error, Runnable callback);

    public abstract Pane errorDialog(Throwable error);

    public abstract Pane errorDialog(String errorMessage);

    public abstract Pane errorDialog(String errorMessage, Runnable callback);

    public abstract Pane progressDialog(String message, ReadOnlyDoubleProperty observable, Runnable callback);

    public abstract Pane progressDialogIndeterminate(String message, Runnable callback);

    public abstract Pane customDialog(String message, Node content, Runnable callback, Button... buttons);
}
