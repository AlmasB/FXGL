/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui.property;

import com.almasb.fxgl.core.collection.UpdatableObjectProperty;
import com.almasb.fxgl.core.math.Vec2;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * // TODO: read-only version?
 * // TODO: empty String check when view is updated
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Vec2PropertyViewFactory implements PropertyViewFactory<Vec2, HBox> {

    private boolean ignoreChangeView = false;
    private boolean ignoreChangeProperty = false;

    @Override
    public HBox makeView(ObjectProperty<Vec2> value) {
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
    public void onPropertyChanged(ObjectProperty<Vec2> value, HBox view) {
        var fieldX = (TextField) view.getChildren().get(0);
        var fieldY = (TextField) view.getChildren().get(1);

        ignoreChangeView = true;

        fieldX.setText(Float.toString(value.getValue().x));
        fieldY.setText(Float.toString(value.getValue().y));

        ignoreChangeView = false;
    }

    @Override
    public void onViewChanged(ObjectProperty<Vec2> value, HBox view) {
        var fieldX = (TextField) view.getChildren().get(0);
        var fieldY = (TextField) view.getChildren().get(1);

        ignoreChangeProperty = true;

        value.getValue().x = Float.parseFloat(fieldX.getText());
        value.getValue().y = Float.parseFloat(fieldY.getText());

        ((UpdatableObjectProperty<Vec2>)value).forceUpdateListeners(value.getValue(), value.getValue());

        ignoreChangeProperty = false;
    }
}
