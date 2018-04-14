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
import com.almasb.fxgl.core.collection.UnorderedArray;

/**
 * A pool of objects that can be reused to avoid allocation.
 * @see Pools
 * @author Nathan Sweet
 */
public abstract class Pool<T> {

    /**
     * The maximum number of objects that will be pooled.
     */
    public final int max;

    /**
     * The highest number of free objects.
     * Can be reset any time.
     */
    private int peak;

    private final Array<T> freeObjects;

    /**
     * Creates a pool with an initial capacity of 16 and no maximum.
     */
    public Pool() {
        this(16, Integer.MAX_VALUE);
    }

    /**
     * Creates a pool with the specified initial capacity and no maximum.
     *
     * @param initialCapacity initial pool capacity
     */
    public Pool(int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    /**
     * Creates a pool with the specified initial capacity and maximum.
     *
     * @param initialCapacity initial pool capacity
     * @param max the maximum number of free objects to store in this pool
     */
    public Pool(int initialCapacity, int max) {
        freeObjects = new UnorderedArray<>(initialCapacity);
        this.max = max;
    }

    /**
     * @return a newly constructed object
     */
    protected abstract T newObject();

    /**
     * The object may be new (from {@link #newObject()}) or reused (previously {@link #free(Object) freed}).
     *
     * @return a pooled object
     */
    public T obtain() {
        return freeObjects.isEmpty() ? newObject() : freeObjects.pop();
    }

    /**
     * Puts the specified object in the pool, making it eligible to be returned by {@link #obtain()}.
     * If the pool already contains {@link #max} free objects, the specified object is reset but not added to the pool.
     *
     * @param object the object to put in the pool
     * @throws IllegalArgumentException if object is null
     */
    public void free(T object) {
        if (object == null)
            throw new IllegalArgumentException("object cannot be null.");

        if (freeObjects.size() < max) {
            freeObjects.add(object);
            peak = Math.max(peak, freeObjects.size());
        }

        reset(object);
    }

    /**
     * Called when an object is freed to clear the state of the object for possible later reuse.
     * The default implementation calls {@link Poolable#reset()} if the object is {@link Poolable}.
     *
     * @param object the object to free
     */
    protected void reset(T object) {
        if (object instanceof Poolable)
            ((Poolable) object).reset();
    }

    /**
     * Puts the specified objects in the pool.
     * Null objects within the array are silently ignored.
     *
     * @see #free(Object)
     * @param objects an array of objects to put back in the pool
     * @throws IllegalArgumentException if objects is null
     */
    public void freeAll(Array<T> objects) {
        if (objects == null)
            throw new IllegalArgumentException("objects cannot be null.");

        Array<T> freeObjects = this.freeObjects;
        int max = this.max;

        for (int i = 0; i < objects.size(); i++) {
            T object = objects.get(i);

            if (object == null)
                continue;

            if (freeObjects.size() < max)
                freeObjects.add(object);

            reset(object);
        }

        peak = Math.max(peak, freeObjects.size());
    }

    /**
     * Removes all free objects from this pool.
     */
    public void clear() {
        freeObjects.clear();
    }

    /**
     * The number of objects available to be obtained.
     */
    public int getFree() {
        return freeObjects.size();
    }
}

