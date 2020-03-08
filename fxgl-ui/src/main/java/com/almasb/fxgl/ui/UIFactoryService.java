/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.core.EngineService;
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
public abstract class UIFactoryService extends EngineService {

    public abstract void registerFontFactory(FontType type, FontFactory fontFactory);

    public abstract MDIWindow newWindow();

    /**
     * @param size font size
     * @return main UI font with given size
     */
    public abstract Font newFont(double size);

    public abstract Font newFont(FontType type, double size);

    public abstract Text newText(String message);

    public abstract Text newText(String message, double fontSize);

    public abstract Text newText(String message, Color textColor, double fontSize);

    public abstract Text newText(String message, Color textColor, FontType type, double fontSize);

    public abstract Text newText(StringExpression textBinding);

    public abstract Button newButton(String text);

    public abstract Button newButton(StringBinding text);

    public abstract <T> ChoiceBox<T> newChoiceBox(ObservableList<T> items);

    public abstract <T> ChoiceBox<T> newChoiceBox();

    public abstract CheckBox newCheckBox();

    public abstract <T> Spinner<T> newSpinner(ObservableList<T> items);

    public abstract <T> ListView<T> newListView(ObservableList<T> items);

    public abstract <T> ListView<T> newListView();

    public abstract FXGLTextFlow newTextFlow();
}
