/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util;

/**
 * Represents a function that accepts three arguments and produces a result.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
