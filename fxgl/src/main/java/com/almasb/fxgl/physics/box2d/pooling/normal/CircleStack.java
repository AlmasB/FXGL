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
package com.almasb.fxgl.physics.box2d.pooling.normal;

import com.almasb.fxgl.physics.box2d.pooling.IOrderedStack;

public abstract class CircleStack<E> implements IOrderedStack<E> {

    private final Object[] pool;
    private int index;
    private final int size;
    private final Object[] container;

    public CircleStack(int argStackSize, int argContainerSize) {
        size = argStackSize;
        pool = new Object[argStackSize];
        for (int i = 0; i < argStackSize; i++) {
            pool[i] = newInstance();
        }
        index = 0;
        container = new Object[argContainerSize];
    }

    @SuppressWarnings("unchecked")
    public final E pop() {
        index++;
        if (index >= size) {
            index = 0;
        }
        return (E) pool[index];
    }

    @SuppressWarnings("unchecked")
    public final E[] pop(int argNum) {
        assert (argNum <= container.length) : "Container array is too small";
        if (index + argNum < size) {
            System.arraycopy(pool, index, container, 0, argNum);
            index += argNum;
        } else {
            int overlap = (index + argNum) - size;
            System.arraycopy(pool, index, container, 0, argNum - overlap);
            System.arraycopy(pool, 0, container, argNum - overlap, overlap);
            index = overlap;
        }
        return (E[]) container;
    }

    @Override
    public void push(int argNum) {
    }

    /** Creates a new instance of the object contained by this stack. */
    protected abstract E newInstance();
}
