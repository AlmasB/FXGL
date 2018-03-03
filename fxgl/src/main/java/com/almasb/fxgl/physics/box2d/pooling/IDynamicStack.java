/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.pooling;

/**
 * Same functionality of a regular java.util stack.  Object
 * return order does not matter.
 * @author Daniel
 *
 * @param <E>
 */
public interface IDynamicStack<E> {

    /**
     * Pops an item off the stack
     * @return
     */
    E pop();

    /**
     * Pushes an item back on the stack
     * @param argObject
     */
    void push(E argObject);
}
