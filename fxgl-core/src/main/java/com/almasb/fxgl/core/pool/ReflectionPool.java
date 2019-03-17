/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.pool;

import com.almasb.fxgl.core.reflect.ReflectionUtils;

public final class ReflectionPool<T> extends Pool<T> {

    private final Class<T> type;

    ReflectionPool(Class<T> type, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.type = type;
    }

    @Override
    protected T newObject() {
        return ReflectionUtils.newInstance(type);
    }
}
