/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/*
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.pool;

import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.collection.Array;

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
}
