/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface StateChangeListener {

    /**
     * Called before passed state enters.
     */
    default void beforeEnter(State state) {}

    /**
     * Called when passed state entered.
     */
    default void entered(State state) {}

    /**
     * Called before passed state exits.
     */
    default void beforeExit(State state) {}

    /**
     * Called when passed state exited.
     */
    default void exited(State state) {}
}
