/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.ecs.CopyableComponent;
import com.almasb.fxgl.ecs.component.BooleanComponent;
import com.almasb.fxgl.ecs.component.Required;

/**
 * Marks an entity as collidable.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
@Required(TypeComponent.class)
@Required(BoundingBoxComponent.class)
public class CollidableComponent extends BooleanComponent implements CopyableComponent<CollidableComponent> {
    public CollidableComponent(boolean collidable) {
        super(collidable);
    }

    public CollidableComponent() {
        this(false);
    }

    @Override
    public CollidableComponent copy() {
        return new CollidableComponent(getValue());
    }
}
