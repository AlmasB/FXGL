/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.ui;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * On/Off toggle switch.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class ToggleSwitch extends Parent {

    private boolean animating = false;

    private ReadOnlyBooleanWrapper switchedOn = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty switchedOnProperty() {
        return switchedOn.getReadOnlyProperty();
    }

    /**
     * @return true if switch is ON, false - if OFF
     */
    public boolean isSwitchedOn() {
        return switchedOnProperty().get();
    }

    private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
    private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

    private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

    private Color colorOn = Color.LIGHTGREEN;

    public ToggleSwitch() {
        Rectangle background = new Rectangle(100, 50);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);
        background.setArcWidth(50);
        background.setArcHeight(50);

        Circle trigger = new Circle(25);
        trigger.setCenterX(25);
        trigger.setCenterY(25);
        trigger.setFill(Color.WHITE);
        trigger.setStroke(Color.LIGHTGRAY);

        translateAnimation.setNode(trigger);
        fillAnimation.setShape(background);

        getChildren().addAll(background, trigger);

        switchedOnProperty().addListener((obs, oldState, isOn) -> {
            if (animating) {
                animation.stop();
            }

            animating = true;

            translateAnimation.setToX(isOn ? 100 - 50 : 0);
            fillAnimation.setFromValue(isOn ? Color.WHITE : colorOn);
            fillAnimation.setToValue(isOn ? colorOn : Color.WHITE);

            animation.setOnFinished(e -> {
                animating = false;
            });
            animation.play();
        });

        setOnMouseClicked(event -> {
            if (animating)
                return;

            switchedOn.set(!isSwitchedOn());
        });
    }

    public void setFill(Color colorOn) {
        this.colorOn = colorOn;
    }
}
