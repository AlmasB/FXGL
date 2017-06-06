/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
