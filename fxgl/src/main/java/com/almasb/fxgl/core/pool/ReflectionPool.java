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

import com.almasb.fxgl.core.reflect.ClassReflection;
import com.almasb.fxgl.core.reflect.Constructor;
import com.almasb.fxgl.core.reflect.ReflectionException;

/**
 * Pool that creates new instances of a type using reflection.
 * The type must have a zero argument constructor.
 * {@link Constructor#setAccessible(boolean)} will be used if the class and/or constructor is not visible.
 *
 * @author Nathan Sweet
 */
public class ReflectionPool<T> extends Pool<T> {

    private final Constructor constructor;

    public ReflectionPool(Class<T> type) {
        this(type, 16, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity) {
        this(type, initialCapacity, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity, int max) {
        super(initialCapacity, max);
        constructor = findConstructor(type);
        if (constructor == null)
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName());
    }

    private Constructor findConstructor(Class<T> type) {
        try {
            return ClassReflection.getConstructor(type, (Class[]) null);
        } catch (Exception ex1) {
            try {
                Constructor constructor = ClassReflection.getDeclaredConstructor(type, (Class[]) null);
                constructor.setAccessible(true);
                return constructor;
            } catch (ReflectionException ex2) {
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected T newObject() {
        try {
            return (T) constructor.newInstance((Object[]) null);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create new instance: " + constructor.getDeclaringClass().getName(), ex);
        }
    }
}
