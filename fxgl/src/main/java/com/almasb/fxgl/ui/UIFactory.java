/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.animation.AnimatedPoint2D;
import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.util.EmptyRunnable;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

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

    default Text newText(StringExpression textBinding) {
        Text text = newText(textBinding.get());
        text.textProperty().bind(textBinding);
        return text;
    }

    default void centerTextX(Text text, double minX, double maxX) {
        text.setTranslateX((minX + maxX) / 2 - text.getLayoutBounds().getWidth() / 2);
    }

    default void centerTextY(Text text, double minY, double maxY) {
        text.setTranslateY((minY + maxY) / 2 - text.getLayoutBounds().getHeight() / 2);
    }

    default void centerText(Text text) {
        centerText(text, FXGL.getAppWidth() / 2, FXGL.getAppHeight() / 2);
    }

    default void centerText(Text text, double x, double y) {
        text.setTranslateX(x - text.getLayoutBounds().getWidth() / 2);
        text.setTranslateY(y - text.getLayoutBounds().getHeight() / 2);
    }

    /**
     * Binds text to application center, i.e. text stays
     * centered regardless of content size.
     *
     * @param text UI object
     */
    default void centerTextBind(Text text) {
        centerTextBind(text, FXGL.getAppWidth() / 2, FXGL.getAppHeight() / 2);
    }

    /**
     * Binds text to given center point, i.e. text stays
     * centered regardless of content size.
     *
     * @param text UI object
     */
    default void centerTextBind(Text text, double x, double y) {
        text.layoutBoundsProperty().addListener((o, old, bounds) -> {
            text.setTranslateX(x - bounds.getWidth() / 2);
            text.setTranslateY(y - bounds.getHeight() / 2);
        });
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
