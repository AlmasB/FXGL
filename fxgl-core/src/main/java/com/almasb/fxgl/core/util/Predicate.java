/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 *
 * @param <T> the type of the input to the predicate
 */
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param arg the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean test(T arg);
}

