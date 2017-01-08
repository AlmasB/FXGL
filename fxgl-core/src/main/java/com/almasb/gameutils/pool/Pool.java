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

package com.almasb.gameutils.pool;

import com.almasb.gameutils.collection.Array;

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
        freeObjects = new Array<>(false, initialCapacity);
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

