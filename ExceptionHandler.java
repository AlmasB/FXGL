/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import java.util.function.Consumer;

/**
 * A service for handling exceptions.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface ExceptionHandler extends Consumer<Throwable> {

    /**
     * Equivalent to <code>handle(e)</code>.
     *
     * @param e exception
     */
    @Override
    default void accept(Throwable e) {
        handle(e);
    }

    /**
     * Handles given checked exception.
     * It is up to the implementation to decide how it should log / display
     * the exception.
     *
     * @param e exception
     */
    void handle(Throwable e);
}
