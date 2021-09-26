/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.pooling;

public abstract class MutableStack<E> implements IDynamicStack<E> {

    private E[] stack = null;
    private int index = 0;
    private int size;

    public MutableStack(int initialSize) {
        extendStack(initialSize);
    }

    private void extendStack(int newSize) {
        E[] newStack = newArray(newSize);
        if (stack != null) {
            System.arraycopy(stack, 0, newStack, 0, size);
        }
        for (int i = 0; i < newStack.length; i++) {
            newStack[i] = newInstance();
        }
        stack = newStack;
        size = newStack.length;
    }

    public final E pop() {
        if (index >= size) {
            extendStack(size * 2);
        }
        return stack[index++];
    }

    public final void push(E argObject) {
        assert index > 0;
        stack[--index] = argObject;
    }

    /** Creates a new instance of the object contained by this stack. */
    protected abstract E newInstance();

    protected abstract E[] newArray(int size);
}
