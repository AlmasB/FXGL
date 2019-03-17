/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components;

import com.almasb.fxgl.entity.component.CoreComponent;
import com.almasb.fxgl.entity.component.SerializableComponent;
import com.almasb.fxgl.core.serialization.Bundle;

import java.io.Serializable;

/**
 * Represents an entity type.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@CoreComponent
public class TypeComponent extends ObjectComponent<Serializable>
        implements SerializableComponent {

    /**
     * Constructs a component with no type.
     */
    public TypeComponent() {
        this(new SObject());
    }

    /**
     * Constructs a component with given type.
     * Note: although the type could be any object, it is recommended
     * that an enum is used to represent types.
     *
     * @param type entity type
     */
    public TypeComponent(Serializable type) {
        super(type);
    }

    /**
     * <pre>
     *     Example:
     *     entity.getTypeComponent().isType(Type.PLAYER);
     * </pre>
     *
     * @param type entity type
     * @return true iff this type component is of given type
     */
    public boolean isType(Object type) {
        return getValue().equals(type);
    }

    @Override
    public String toString() {
        return "Type(" + getValue() + ")";
    }

    @Override
    public void write(Bundle bundle) {
        bundle.put("value", getValue());
    }

    @Override
    public void read(Bundle bundle) {
        setValue(bundle.get("value"));
    }

    private static class SObject implements Serializable {
        private static final long serialVersionUID = -1L;

        @Override
        public String toString() {
            return "NONE";
        }
    }
}
