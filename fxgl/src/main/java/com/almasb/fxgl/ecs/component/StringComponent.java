/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs.component;

import com.almasb.fxgl.ecs.AbstractComponent;
import com.almasb.fxgl.ecs.serialization.SerializableComponent;
import com.almasb.fxgl.io.serialization.Bundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a String value based component.
 * <p>
 * <pre>
 * Example:
 *
 * public class NameComponent extends StringComponent {
 *      public NameComponent(String initialValue) {
 *          super(initialValue);
 *      }
 * }
 *
 * Entity player = ...
 * player.addComponent(new NameComponent("PlayerName"));
 *
 * String name = player.getComponent(NameComponent.class).getValue();
 *
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class StringComponent extends AbstractComponent implements SerializableComponent {
    private StringProperty property;

    /**
     * No-arg ctor, initializes the value to empty string.
     */
    public StringComponent() {
        this("");
    }

    /**
     * Constructs a string value component with given
     * initial value.
     *
     * @param initialValue the initial value
     */
    public StringComponent(String initialValue) {
        property = new SimpleStringProperty(initialValue);
    }

    /**
     * @return value property
     */
    public final StringProperty valueProperty() {
        return property;
    }

    /**
     * @return value held by this component
     */
    public final String getValue() {
        return property.get();
    }

    /**
     * Set value to this component.
     *
     * @param value new value
     */
    public final void setValue(String value) {
        property.set(value);
    }

    @Override
    public void write(@NotNull Bundle bundle) {
        bundle.put("value", getValue());
    }

    @Override
    public void read(@NotNull Bundle bundle) {
        setValue(bundle.get("value"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[value=" + getValue() + "]";
    }
}
