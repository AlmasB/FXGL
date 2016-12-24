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

package com.almasb.fxgl.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
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

    Button newButton(String text);

    <T> ChoiceBox<T> newChoiceBox(ObservableList<T> items);

    <T> ChoiceBox<T> newChoiceBox();

    CheckBox newCheckBox();

    <T> Spinner<T> newSpinner(ObservableList<T> items);
}
