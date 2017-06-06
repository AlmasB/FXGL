/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs.component;

import com.almasb.fxgl.ecs.AbstractComponent;
import com.almasb.fxgl.ecs.serialization.SerializableComponent;
import com.almasb.fxgl.io.serialization.Bundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a boolean value based component.
 * <p>
 * <pre>
 * Example:
 *
 * public class GravityComponent extends BooleanComponent {
 *      public GravityComponent(boolean initialValue) {
 *          super(initialValue);
 *      }
 * }
 *
 * Entity player = ...
 * player.addComponent(new GravityComponent(true));
 *
 * boolean gravityEnabled = player.getComponent(GravityComponent.class).getValue();
 *
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class BooleanComponent extends AbstractComponent implements SerializableComponent {
    private BooleanProperty property;

    /**
     * No-arg ctor, initializes the value to false.
     */
    public BooleanComponent() {
        this(false);
    }

    /**
     * Constructs a boolean value component with given
     * initial value.
     *
     * @param initialValue initial value
     */
    public BooleanComponent(boolean initialValue) {
        property = new SimpleBooleanProperty(initialValue);
    }

    /**
     * @return value property
     */
    public final BooleanProperty valueProperty() {
        return property;
    }

    /**
     * @return value held by this component
     */
    public final boolean getValue() {
        return property.get();
    }

    /**
     * Set value of this component.
     *
     * @param value new value
     */
    public final void setValue(boolean value) {
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
