/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.entity.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ComponentHelper {

    /**
     * Equivalent to component.setEntity(entity).
     * This allows us to hide setEntity on Component.
     */
    public static void setEntity(Component component, Entity entity) {
        component.setEntity(entity);
    }
}
