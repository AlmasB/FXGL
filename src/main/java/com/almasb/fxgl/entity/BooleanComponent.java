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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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
 * boolean money = player.getComponent(GravityComponent.class).getValue();
 *
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class BooleanComponent implements Component {
    private BooleanProperty property;

    /**
     * No-arg ctor, initializes the value to empty string.
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
     * Set value to this component.
     *
     * @param value new value
     */
    public final void setValue(boolean value) {
        property.set(value);
    }
}
