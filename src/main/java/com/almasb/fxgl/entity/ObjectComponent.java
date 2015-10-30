/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a Object value based component.
 * <p>
 * <pre>
 * Example:
 *
 * public class WeaponComponent extends ObjectComponent<Weapon> {
 *      public WeaponComponent(Weapon initialValue) {
 *          super(initialValue);
 *      }
 * }
 *
 * Entity player = ...
 * Weapon weapon = ...
 * player.addComponent(new WeaponComponent(weapon));
 *
 * Weapon w = player.getComponent(WeaponComponent.class).getValue();
 *
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class ObjectComponent<T> implements Component {
    private ObjectProperty<T> property;

    /**
     * No-arg ctor, initializes the value to null.
     */
    public ObjectComponent() {
        this(null);
    }

    /**
     * Constructs an object value component with given
     * initial value.
     *
     * @param initialValue the initial value
     */
    public ObjectComponent(T initialValue) {
        property = new SimpleObjectProperty<>(initialValue);
    }

    /**
     * @return value property
     */
    public final ObjectProperty<T> valueProperty() {
        return property;
    }

    /**
     * @return value held by this component
     */
    public final T getValue() {
        return property.get();
    }

    /**
     * Set value to this component.
     *
     * @param value new value
     */
    public final void setValue(T value) {
        property.set(value);
    }
}
