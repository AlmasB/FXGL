/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core;

/**
 * Marks a type whose instances can be copied.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Copyable<T> {

    T copy();
}
