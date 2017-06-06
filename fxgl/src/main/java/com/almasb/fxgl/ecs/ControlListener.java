/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

/**
 * Listener for control related events.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface ControlListener {

    /**
     * Called after control was added to an entity.
     *
     * @param control the control that was added
     */
    void onControlAdded(Control control);

    /**
     * Called before control is removed from an entity.
     *
     * @param control the control to be removed
     */
    void onControlRemoved(Control control);
}
