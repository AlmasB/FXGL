/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components;

import com.almasb.fxgl.entity.component.CopyableComponent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Marks an entity as collidable.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class CollidableComponent extends BooleanComponent implements CopyableComponent<CollidableComponent> {

    private List<Serializable> ignoredTypes = new ArrayList<>();

    public CollidableComponent(boolean collidable) {
        super(collidable);
    }

    public List<Serializable> getIgnoredTypes() {
        return ignoredTypes;
    }

    public void addIgnoredType(Serializable type) {
        ignoredTypes.add(type);
    }

    public void removeIgnoredType(Serializable type) {
        ignoredTypes.remove(type);
    }

    @Override
    public CollidableComponent copy() {
        // TODO: copy ignored types also
        return new CollidableComponent(getValue());
    }
}
