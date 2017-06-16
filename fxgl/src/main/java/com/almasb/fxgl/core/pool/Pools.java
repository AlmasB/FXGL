/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/*
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.pool;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;

/**
 * Stores a map of {@link Pool}s (usually {@link ReflectionPool}s) by type for convenient static access.
 *
 * @author Nathan Sweet
 */
public final class Pools {

    private Pools() {
        // no instances
    }

    private static final ObjectMap<Class, Pool> typePools = new ObjectMap<>();

    /**
     * Returns a new or existing pool for the specified type, stored in a Class to {@link Pool} map.
     * Note the max size is ignored if this is not the first time this pool has been requested.
     */
    public static <T> Pool<T> get(Class<T> type, int max) {
        Pool pool = typePools.get(type);
        if (pool == null) {
            pool = new ReflectionPool<>(type, 4, max);
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
    public static <T> Pool<T> get(Class<T> type) {
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
    public static void free(Object object) {
        if (object == null)
            throw new IllegalArgumentException("Object cannot be null.");

        Pool pool = typePools.get(object.getClass());
        if (pool == null)
            return; // Ignore freeing an object that was never retained.

        pool.free(object);
    }

    /**
     * Frees the specified objects from the {@link #get(Class) pool}.
     * Null objects within the array are silently ignored.
     * Objects don't need to be from the same pool.
     *
     * @param objects objects to be freed
     */
    public static void freeAll(Array objects) {
        freeAll(objects, false);
    }

    /**
     * Frees the specified objects from the {@link #get(Class) pool}.
     * Null objects within the array are silently ignored.
     *
     * @param objects objects to free
     * @param samePool if false, objects don't need to be from the same pool but the pool must be looked up for each object
     */
    public static void freeAll(Array objects, boolean samePool) {
        if (objects == null)
            throw new IllegalArgumentException("Objects cannot be null.");

        Pool pool = null;
        for (int i = 0, n = objects.size(); i < n; i++) {
            Object object = objects.get(i);
            if (object == null)
                continue;

            if (pool == null) {
                pool = typePools.get(object.getClass());
                if (pool == null)
                    continue; // Ignore freeing an object that was never retained.
            }

            pool.free(object);

            if (!samePool)
                pool = null;
        }
    }

    /**
     * Frees all given objects.
     *
     * @param objects to free
     */
    public static void freeAll(Object... objects) {
        for (Object obj : objects)
            free(obj);
    }
}
