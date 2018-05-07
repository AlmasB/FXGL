/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components;

import com.almasb.fxgl.entity.component.CopyableComponent;
import com.almasb.fxgl.entity.component.Required;

/**
 * Marks an entity as collidable.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class CollidableComponent extends BooleanComponent implements CopyableComponent<CollidableComponent> {
    public CollidableComponent(boolean collidable) {
        super(collidable);
    }

    @Override
    public CollidableComponent copy() {
        return new CollidableComponent(getValue());
    }
}
