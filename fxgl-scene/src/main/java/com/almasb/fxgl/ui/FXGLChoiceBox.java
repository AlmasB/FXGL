/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

/**
 * JavaFX ChoiceBox styled with FXGL CSS.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXGLChoiceBox<T> extends ChoiceBox<T> {

    public FXGLChoiceBox() {
        super();
        getStyleClass().add("fxgl-choice-box");
    }

    public FXGLChoiceBox(ObservableList<T> items) {
        super(items);
        getStyleClass().add("fxgl-choice-box");
    }

    @Override
    protected double computePrefWidth(double height) {
        return 200;
    }
}
