/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.ecs.component;

import com.almasb.fxgl.io.serialization.Bundle;
import com.almasb.fxgl.ecs.AbstractComponent;
import com.almasb.fxgl.ecs.serialization.SerializableComponent;
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
