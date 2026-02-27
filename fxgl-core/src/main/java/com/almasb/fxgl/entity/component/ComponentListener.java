/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

/**
 * Notifies when a component is added / removed.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface ComponentListener {

    /**
     * Called after the component was added.
     */
    void onAdded(Component component);

    /**
     * Called before the component is removed.
     */
    void onRemoved(Component component);
}
