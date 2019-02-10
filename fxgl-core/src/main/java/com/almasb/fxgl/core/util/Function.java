/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util;

/**
 * Represents a function that accepts one argument and produces a result.
 * Copied from java.util.function.Function
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param arg the function argument
     * @return the function result
     */
    R apply(T arg);
}
