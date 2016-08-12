/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.util;

import com.almasb.gameutils.pool.Pool;

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
     * Make the pooler use the given supplier to produce
     * instances for given type.
     * By default public no-arg constructor is used.
     *
     * @param type
     * @param pool
     * @param <T>
     */
    <T> void registerPool(Class<T> type, Pool<T> pool);
}
