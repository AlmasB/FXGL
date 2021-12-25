/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ui.property.Vec2PropertyViewChangeListener;
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

    public static final Map<Class<?>, PropertyViewChangeListener<?, ?>> converters = new HashMap<>();

    static {
        addViewConverter(Vec2.class, new Vec2PropertyViewChangeListener());
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
