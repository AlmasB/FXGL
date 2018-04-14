/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.listener;

import com.almasb.fxgl.app.State;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface StateListener {

    default void onEnter(State prevState) {}

    void onUpdate(double tpf);

    default void onExit() {}
}
