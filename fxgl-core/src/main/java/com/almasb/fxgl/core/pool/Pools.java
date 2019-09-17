/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/*
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.pool;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores a map of {@link Pool}s (usually {@link ReflectionPool}s) by type for convenient static access.
 *
 * @author Nathan Sweet
 */
public final class Pools {

    private Pools() {
        // no instances
    }

    private static final Map<Class, Pool> typePools = new HashMap<>();

    /**
     * Returns a new or existing pool for the specified type, stored in a Class to {@link Pool} map.
     * Note the max size is ignored if this is not the first time this pool has been requested.
     */
    @SuppressWarnings("unchecked")
    private static <T> Pool<T> get(Class<T> type, int max) {
        Pool pool = typePools.get(type);
        if (pool == null) {
            pool = new ReflectionPool<>(type, 16, max);
            typePools.put(type, pool);
        }
        return pool;
    }

    /**
     * The max size of the pool used is 100.
     *
     * @param type pool class
     * @param <T> pool type
     * @return a new or existing pool for the specified type, stored in a Class to {@link Pool} map
     */
    private static <T> Pool<T> get(Class<T> type) {
        return get(type, 100);
    }

    /**
     * Sets an existing pool for the specified type, stored in a Class to {@link Pool} map.
     *
     * @param type pool class
     * @param pool pool object
     * @param <T> pool type
     */
    public static <T> void set(Class<T> type, Pool<T> pool) {
        typePools.put(type, pool);
    }

    /**
     * @param type object class
     * @param <T> object type
     * @return an object from the {@link #get(Class) pool}
     */
    public static <T> T obtain(Class<T> type) {
        return get(type).obtain();
    }

    /**
     * Frees an object from the {@link #get(Class) pool}.
     *
     * @param object the object to free
     */
    @SuppressWarnings("unchecked")
    public static void free(Object object) {
        Pool pool = typePools.get(object.getClass());
        if (pool == null)
            return; // Ignore freeing an object that was never retained.

        pool.free(object);
    }
}
