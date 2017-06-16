/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service;

import com.almasb.fxgl.animation.AnimatedPoint2D;
import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.util.EmptyRunnable;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    default Animation<?> translate(Node node, Point2D to, Duration duration) {
        return translate(node, new Point2D(node.getTranslateX(), node.getTranslateY()), to, Duration.ZERO, duration);
    }

    default Animation<?> translate(Node node, Point2D from, Point2D to, Duration duration) {
        return translate(node, from, to, Duration.ZERO, duration);
    }

    default Animation<?> translate(Node node, Point2D from, Point2D to, Duration delay, Duration duration) {
        return translate(node, from, to, delay, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> translate(Node node, Point2D from, Point2D to, Duration delay, Duration duration, Runnable onFinishedAction) {
        Animation<?> anim = new Animation<Point2D>(delay, duration, 1, new AnimatedPoint2D(from, to)) {

            @Override
            public void onProgress(Point2D value) {
                node.setTranslateX(value.getX());
                node.setTranslateY(value.getY());
            }
        };
        anim.setOnFinished(onFinishedAction);
        return anim;
    }

    default Animation<?> fadeIn(Node node, Duration duration) {
        return fadeIn(node, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> fadeIn(Node node, Duration duration, Runnable onFinishedAction) {
        return fadeIn(node, Duration.ZERO, duration, onFinishedAction);
    }

    default Animation<?> fadeIn(Node node, Duration delay, Duration duration) {
        return fadeIn(node, delay, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> fadeIn(Node node, Duration delay, Duration duration, Runnable onFinishedAction) {
        Animation<?> anim = new Animation<Double>(delay, duration, 1, new AnimatedValue<>(0.0, 1.0)) {
            @Override
            public void onProgress(Double value) {
                node.setOpacity(value);
            }
        };
        anim.setOnFinished(onFinishedAction);
        return anim;
    }

    default Animation<?> fadeOut(Node node, Duration duration) {
        return fadeOut(node, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> fadeOut(Node node, Duration duration, Runnable onFinishedAction) {
        return fadeOut(node, Duration.ZERO, duration, onFinishedAction);
    }

    default Animation<?> fadeOut(Node node, Duration delay, Duration duration) {
        return fadeOut(node, delay, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> fadeOut(Node node, Duration delay, Duration duration, Runnable onFinishedAction) {
        Animation<?> anim = fadeIn(node, delay, duration, onFinishedAction);

        // fade out is reverse fade in
        anim.setReverse(true);
        return anim;
    }

    default Animation<?> fadeInOut(Node node, Duration duration) {
        return fadeInOut(node, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> fadeInOut(Node node, Duration duration, Runnable onFinishedAction) {
        return fadeInOut(node, Duration.ZERO, duration, onFinishedAction);
    }

    default Animation<?> fadeInOut(Node node, Duration delay, Duration duration) {
        return fadeInOut(node, delay, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> fadeInOut(Node node, Duration delay, Duration duration, Runnable onFinishedAction) {
        Animation<?> anim = fadeIn(node, delay, duration, onFinishedAction);
        anim.setCycleCount(2);
        anim.setAutoReverse(true);
        return anim;
    }

    default Animation<?> fadeOutIn(Node node, Duration duration) {
        return fadeOutIn(node, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> fadeOutIn(Node node, Duration duration, Runnable onFinishedAction) {
        return fadeOutIn(node, Duration.ZERO, duration, onFinishedAction);
    }

    default Animation<?> fadeOutIn(Node node, Duration delay, Duration duration) {
        return fadeOutIn(node, delay, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> fadeOutIn(Node node, Duration delay, Duration duration, Runnable onFinishedAction) {
        Animation<?> anim = fadeInOut(node, delay, duration, onFinishedAction);

        // fade out in is reverse fade in out
        anim.setReverse(true);
        return anim;
    }

    default Animation<?> scale(Node node, Point2D to, Duration duration) {
        return scale(node, new Point2D(node.getScaleX(), node.getScaleY()), to, Duration.ZERO, duration);
    }

    default Animation<?> scale(Node node, Point2D from, Point2D to, Duration duration) {
        return scale(node, from, to, Duration.ZERO, duration);
    }

    default Animation<?> scale(Node node, Point2D from, Point2D to, Duration delay, Duration duration) {
        return scale(node, from, to, delay, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> scale(Node node, Point2D from, Point2D to, Duration delay, Duration duration, Runnable onFinishedAction) {
        Animation<?> anim = new Animation<Point2D>(delay, duration, 1, new AnimatedPoint2D(from, to)) {

            @Override
            public void onProgress(Point2D value) {
                node.setScaleX(value.getX());
                node.setScaleY(value.getY());
            }
        };
        anim.setOnFinished(onFinishedAction);
        return anim;
    }

    default Animation<?> rotate(Node node, double to, Duration duration) {
        return rotate(node, node.getRotate(), to, Duration.ZERO, duration);
    }

    default Animation<?> rotate(Node node, double from, double to, Duration duration) {
        return rotate(node, from, to, Duration.ZERO, duration);
    }

    default Animation<?> rotate(Node node, double from, double to, Duration delay, Duration duration) {
        return rotate(node, from, to, delay, duration, EmptyRunnable.INSTANCE);
    }

    default Animation<?> rotate(Node node, double from, double to, Duration delay, Duration duration, Runnable onFinishedAction) {
        Animation<?> anim = new Animation<Double>(delay, duration, 1, new AnimatedValue<>(from, to)) {

            @Override
            public void onProgress(Double value) {
                node.setRotate(value);
            }
        };
        anim.setOnFinished(onFinishedAction);
        return anim;
    }
}
