/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs.component;

/**
 * Can be used to store user specific data to add as component
 * to an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class UserDataComponent extends ObjectComponent<Object> {
    public UserDataComponent(Object data) {
        super(data);
    }
}
