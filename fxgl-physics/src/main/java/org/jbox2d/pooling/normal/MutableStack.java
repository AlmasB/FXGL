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
package org.jbox2d.pooling.normal;

import org.jbox2d.pooling.IDynamicStack;

public abstract class MutableStack<E> implements IDynamicStack<E> {

    private E[] stack;
    private int index;
    private int size;

    public MutableStack(int argInitSize) {
        index = 0;
        stack = null;
        index = 0;
        extendStack(argInitSize);
    }

    private void extendStack(int argSize) {
        E[] newStack = newArray(argSize);
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
        assert (index > 0);
        stack[--index] = argObject;
    }

    /** Creates a new instance of the object contained by this stack. */
    protected abstract E newInstance();

    protected abstract E[] newArray(int size);
}
