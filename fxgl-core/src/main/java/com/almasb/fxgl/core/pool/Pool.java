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
    private final int max;

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
     */
    public void free(T object) {
        if (freeObjects.size() < max) {
            freeObjects.add(object);
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
     * Removes all free objects from this pool.
     */
    public void clear() {
        freeObjects.clear();
    }
}

