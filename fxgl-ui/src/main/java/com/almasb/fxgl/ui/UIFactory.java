/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import javafx.beans.binding.StringBinding;
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

    void registerFontFactory(FontType type, FontFactory fontFactory);

    MDIWindow newWindow();

    /**
     * @param size font size
     * @return main UI font with given size
     */
    Font newFont(double size);

    Font newFont(FontType type, double size);

    Text newText(String message);

    Text newText(String message, double fontSize);

    Text newText(String message, Color textColor, double fontSize);

    Text newText(StringExpression textBinding);

    Button newButton(String text);

    Button newButton(StringBinding text);

    <T> ChoiceBox<T> newChoiceBox(ObservableList<T> items);

    <T> ChoiceBox<T> newChoiceBox();

    CheckBox newCheckBox();

    <T> Spinner<T> newSpinner(ObservableList<T> items);

    <T> ListView<T> newListView(ObservableList<T> items);

    <T> ListView<T> newListView();

    FXGLTextFlow newTextFlow();
}
