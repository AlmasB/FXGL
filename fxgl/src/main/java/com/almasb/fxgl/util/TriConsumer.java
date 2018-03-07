/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util;

/**
 * Represents a function that accepts three arguments and does not produce a result.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);
}
