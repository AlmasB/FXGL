/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.SerializableComponent;
import com.almasb.fxgl.io.serialization.Bundle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a double value based component.
 * <p>
 * <pre>
 * Example:
 *
 * public class AttackSpeedComponent extends DoubleComponent {
 *      public AttackSpeedComponent(double initialValue) {
 *          super(initialValue);
 *      }
 * }
 *
 * Entity player = ...
 * player.addComponent(new AttackSpeedComponent(1.75));
 *
 * double attackSpeed = player.getComponent(AttackSpeedComponent.class).getValue();
 *
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class DoubleComponent extends Component implements SerializableComponent {
    private DoubleProperty property;

    /**
     * No-arg ctor, initializes the value to 0.
     */
    public DoubleComponent() {
        this(0);
    }

    /**
     * Constructs a double value component with given
     * initial value.
     *
     * @param initialValue initial value
     */
    public DoubleComponent(double initialValue) {
        property = new SimpleDoubleProperty(initialValue);
    }

    /**
     * @return value property
     */
    public final DoubleProperty valueProperty() {
        return property;
    }

    /**
     * @return value held by this component
     */
    public final double getValue() {
        return property.get();
    }

    /**
     * Set value to this component.
     *
     * @param value new value
     */
    public final void setValue(double value) {
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
