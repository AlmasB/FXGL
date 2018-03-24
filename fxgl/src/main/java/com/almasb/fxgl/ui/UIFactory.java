/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Factory service for creating UI controls.
 * Used to unify the look across FXGL.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface UIFactory {

    // TODO: remove defaults

    MDIWindow newWindow();

    /**
     * @param size font size
     * @return main UI font with given size
     */
    Font newFont(double size);

    default Text newText(String message) {
        return newText(message, Color.WHITE, 18);
    }

    default Text newText(String message, double fontSize) {
        return newText(message, Color.WHITE, fontSize);
    }

    default Text newText(String message, Color textColor, double fontSize) {
        Text text = new Text(message);
        text.setFill(textColor);
        text.setFont(newFont(fontSize));
        return text;
    }

    default Text newText(StringExpression textBinding) {
        Text text = newText(textBinding.get());
        text.textProperty().bind(textBinding);
        return text;
    }

    Button newButton(String text);

    <T> ChoiceBox<T> newChoiceBox(ObservableList<T> items);

    <T> ChoiceBox<T> newChoiceBox();

    CheckBox newCheckBox();

    <T> Spinner<T> newSpinner(ObservableList<T> items);

    <T> ListView<T> newListView(ObservableList<T> items);

    <T> ListView<T> newListView();

    default FXGLTextFlow newTextFlow() {
        return new FXGLTextFlow();
    }
}
