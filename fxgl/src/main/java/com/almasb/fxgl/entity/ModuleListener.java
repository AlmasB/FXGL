/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

/**
 * Notifies when a component or a control is added / removed.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface ModuleListener {

    void onAdded(Control control);

    void onRemoved(Control control);

    void onAdded(Component component);

    void onRemoved(Component component);
}
