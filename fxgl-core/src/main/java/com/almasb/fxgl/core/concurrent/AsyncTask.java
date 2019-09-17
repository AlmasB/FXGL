/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class AsyncTask<T> {

    /**
     * Blocks current thread to wait for the result of
     * this async task.
     *
     * @return task result
     */
    public abstract T await();
}
