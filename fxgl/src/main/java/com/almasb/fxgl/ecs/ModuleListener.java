/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface ModuleListener {

    default void onAdded(Control control) {}

    default void onRemoved(Control control) {}

    default void onAdded(Component component) {}

    default void onRemoved(Component component) {}
}
