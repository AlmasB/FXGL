/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/*
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.pool;

/**
 * Objects implementing this interface will have {@link #reset()} called when passed to {@link Pool#free(Object)}.
 */
public interface Poolable {

    /**
     * Resets the object for reuse.
     * Object references should be nulled and fields may be set to default values.
     */
    void reset();
}
