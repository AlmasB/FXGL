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

package com.almasb.ents.component;

import com.almasb.easyio.serialization.Bundle;
import com.almasb.ents.AbstractComponent;
import com.almasb.ents.serialization.SerializableComponent;
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
