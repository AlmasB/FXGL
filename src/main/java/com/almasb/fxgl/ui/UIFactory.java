/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import com.almasb.fxgl.asset.FXGLAssets;
import com.almasb.fxgl.asset.FontFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A collection of static methods that return UI controls to unify
 * the look across FXGL.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class UIFactory {
    private UIFactory() {}

    public static Font newFont(double size) {
        return FXGLAssets.UI_FONT.newFont(size);
    }

    public static Text newText(String message) {
        return newText(message, Color.WHITE, 18);
    }

    public static Text newText(String message, double fontSize) {
        return newText(message, Color.WHITE, fontSize);
    }

    public static Text newText(String message, Color textColor, double fontSize) {
        Text text = new Text(message);
        text.setFill(textColor);
        text.setFont(newFont(fontSize));
        return text;
    }

    public static Button newButton(String text) {
        return new FXGLButton(text);
    }

    public static <T> ChoiceBox<T> newChoiceBox(ObservableList<T> items) {
        return new FXGLChoiceBox<>(items);
    }

    public static <T> ChoiceBox<T> newChoiceBox() {
        return new FXGLChoiceBox<>();
    }

    public static double widthOf(String text, double fontSize) {
        return newText(text, fontSize).getLayoutBounds().getWidth();
    }

    public static double heightOf(String text, double fontSize) {
        return newText(text, fontSize).getLayoutBounds().getHeight();
    }
}
