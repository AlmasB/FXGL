/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.component;

import com.almasb.fxgl.ecs.component.ObjectComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class OwnerComponent extends ObjectComponent<Object> {
    public OwnerComponent(Object entity) {
        super(entity);
    }
}
