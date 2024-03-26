/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui.property;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * // TODO: read-only version?
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Point2DPropertyViewFactory implements PropertyViewFactory<Point2D, HBox> {

    private boolean ignoreChangeView = false;
    private boolean ignoreChangeProperty = false;

    @Override
    public HBox makeView(ObjectProperty<Point2D> value) {
        var fieldX = new TextField();
        var fieldY = new TextField();
        HBox view = new HBox(fieldX, fieldY);

        value.addListener((obs, o, newValue) -> {
            if (ignoreChangeProperty)
                return;

            onPropertyChanged(value, view);
        });

        fieldX.textProperty().addListener((obs, o, x) -> {
            if (ignoreChangeView)
                return;

            onViewChanged(value, view);
        });

        fieldY.textProperty().addListener((obs, o, y) -> {
            if (ignoreChangeView)
                return;

            onViewChanged(value, view);
        });

        onPropertyChanged(value, view);

        return view;
    }

    @Override
    public void onPropertyChanged(ObjectProperty<Point2D> value, HBox view) {
        var fieldX = (TextField) view.getChildren().get(0);
        var fieldY = (TextField) view.getChildren().get(1);

        ignoreChangeView = true;

        fieldX.setText(Double.toString(value.getValue().getX()));
        fieldY.setText(Double.toString(value.getValue().getY()));

        ignoreChangeView = false;
    }

    @Override
    public void onViewChanged(ObjectProperty<Point2D> value, HBox view) {
        var fieldX = (TextField) view.getChildren().get(0);
        var fieldY = (TextField) view.getChildren().get(1);

        ignoreChangeProperty = true;

        var newPoint = new Point2D(
                Double.parseDouble(fieldX.getText()),
                Double.parseDouble(fieldY.getText())
        );

        value.setValue(newPoint);

        ignoreChangeProperty = false;
    }
}
