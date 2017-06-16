/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.pooling.stacks;

public class DynamicIntStack {

    private int[] stack;
    private int size;
    private int position;

    public DynamicIntStack(int initialSize) {
        stack = new int[initialSize];
        position = 0;
        size = initialSize;
    }

    public void reset() {
        position = 0;
    }

    public int pop() {
        assert (position > 0);
        return stack[--position];
    }

    public void push(int i) {
        if (position == size) {
            int[] old = stack;
            stack = new int[size * 2];
            size = stack.length;
            System.arraycopy(old, 0, stack, 0, old.length);
        }
        stack[position++] = i;
    }

    public int getCount() {
        return position;
    }
}
