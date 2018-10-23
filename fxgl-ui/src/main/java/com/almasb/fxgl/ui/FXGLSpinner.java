/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXGLSpinner<T> extends Spinner<T> {

    public FXGLSpinner(ObservableList<T> items) {
        super(items);
        getStyleClass().setAll("fxgl-spinner");
        getEditor().setFont(FXGLUIConfig.getUIFactory().newFont(18));
    }
}
