package com.almasb.ents.component;

import com.almasb.ents.AbstractComponent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class ObjectComponent<T> extends AbstractComponent {
    private ObjectProperty<T> property;

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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[value=" + getValue() + "]";
    }
}
