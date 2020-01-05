/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.core.collection.PropertyMap;
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

/**
 * @author Charly Zhu (charlyzhu@hotmail.com)
 */
public class PropertyMapView extends Parent {
    /**
     * Creates a editable property map view.
     */
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
        this.getChildren().add(rootBox);
    }
    
    /**
     * Gets the display (Node) for the value object.
     * Returns check box when it is a boolean value and text field when it is string etc.
     */
    private Node makeView(Object value) {
        if (value instanceof BooleanProperty) {
            CheckBox box = new CheckBox();
            box.selectedProperty().bindBidirectional((BooleanProperty) value);
            return box;
        }
        else if (value instanceof ObjectProperty) {
            ObjectProperty property = (ObjectProperty) value;
            
            // If object is an enum.
            if (property.get().getClass().isEnum())
                return getEnumDisplay(property);
            
            // If object is something else.
            Text text = new Text();
            text.textProperty().bind(property.asString());
            return text;
        }
        else {
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
    
    private Node getEnumDisplay (ObjectProperty enumProperty) {
        Enum enumValue = (Enum) enumProperty.get();
        
        ObservableList<Enum<?>> list = FXCollections.observableArrayList();
        for (Object anEnum : enumValue.getDeclaringClass().getEnumConstants()) {
            list.add((Enum) anEnum);
        }
    
        ChoiceBox<Enum<?>> display = new ChoiceBox<>();
        display.setItems(list);
        display.setValue(enumValue);
        display.valueProperty().bindBidirectional(enumProperty);
        
        return display;
    }
}
