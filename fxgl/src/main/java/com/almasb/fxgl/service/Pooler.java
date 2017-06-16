/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service;

import com.almasb.fxgl.core.pool.Pool;

/**
 * Pooler service.
 * Allows users to get an instance of a class and pool it for future use.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Pooler {

    /**
     * Either an existing "free" pooled instance is returned
     * or a new instance will be created.
     * <p>
     * Note: type class must have a public no-arg constructor.
     *
     * @param type type class
     * @param <T>  type
     * @return pooled instance of given type
     */
    <T> T get(Class<T> type);

    /**
     * Put the given object back to pool so it can reused.
     * The object will now be managed by the pool.
     * After this call no attempt should be made to use the object.
     * Any instance level fields must be "nulled".
     *
     * @param object the instance to return to pool
     */
    void put(Object object);

    /**
     * Make the pooler use the given pool for given type.
     *
     * @param type the object class
     * @param pool the pool to use
     * @param <T> type
     */
    <T> void registerPool(Class<T> type, Pool<T> pool);
}
