/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.core.collection.UpdatableObjectProperty;
import com.almasb.fxgl.core.math.Vec2;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Charly Zhu (charlyzhu@hotmail.com)
 */
public class PropertyMapView extends Parent {

    private static final Map<Class<?>, PropertyViewChangeListener<?, ?>> converters = new HashMap<>();

    static {
        PropertyViewChangeListener<Vec2, HBox> l = new PropertyViewChangeListener<>() {

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
        };

        addViewConverter(Vec2.class, l);
    }

    public static <T> void addViewConverter(Class<T> type, PropertyViewChangeListener<T, ?> converter) {
        converters.put(type, converter);
    }

    public PropertyMapView(PropertyMap map) {
        VBox rootBox = new VBox();
        rootBox.setBackground(new Background(new BackgroundFill(Color.gray(1.0), CornerRadii.EMPTY, Insets.EMPTY)));

        for (String key : map.keys()) {
            HBox propertyBox = new HBox();
        
            Text name = new Text(key);
            name.setWrappingWidth(150);
            Node value = makeView(map.getValueObservable(key));
        
            propertyBox.getChildren().add(name);
            propertyBox.getChildren().add(value);
            rootBox.getChildren().add(propertyBox);
        }

        getChildren().add(rootBox);
    }

    /**
     * Makes a check box when it is a boolean value and text field when it is string etc.
     */
    @SuppressWarnings("unchecked")
    private Node makeView(Object value) {
        if (value instanceof BooleanProperty) {
            CheckBox box = new CheckBox();
            box.selectedProperty().bindBidirectional((BooleanProperty) value);
            return box;
        } else if (value instanceof ObjectProperty) {
            ObjectProperty<?> property = (ObjectProperty<?>) value;
            
            // If object is an enum.
            if (property.get().getClass().isEnum())
                return makeEnumView((ObjectProperty<Enum<?>>) property);
            
            // If object is something else.
            if (converters.containsKey(property.get().getClass())) {
                PropertyViewChangeListener<?, ?> converter = converters.get(property.get().getClass());

                return converter.makeViewInternal(property);

            } else {
                // If object does not have a converter.

                Text text = new Text();
                text.textProperty().bind(property.asString());
                return text;
            }

        } else {
            TextField textField = new TextField();
            textField.setPrefWidth(150);
            
            if (value instanceof StringProperty)
                textField.textProperty().bindBidirectional((StringProperty) value, new DefaultStringConverter());
            if (value instanceof IntegerProperty)
                textField.textProperty().bindBidirectional((Property<Integer>) value, new IntegerStringConverter());
            if (value instanceof DoubleProperty)
                textField.textProperty().bindBidirectional((Property<Double>) value, new DoubleStringConverter());
            
            return textField;
        }
    }
    
    private Node makeEnumView(ObjectProperty<Enum<?>> enumProperty) {
        Enum<?> enumValue = enumProperty.get();
        
        ObservableList<Enum<?>> list = FXCollections.observableArrayList();
        for (Object anEnum : enumValue.getDeclaringClass().getEnumConstants()) {
            list.add((Enum<?>) anEnum);
        }
    
        ChoiceBox<Enum<?>> view = new ChoiceBox<>();
        view.setItems(list);
        view.setValue(enumValue);
        view.valueProperty().bindBidirectional(enumProperty);
        
        return view;
    }
}
