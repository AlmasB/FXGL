/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.state;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class State {

    /**
     * Called after entering this state from prevState
     */
    protected void onEnter(State prevState) {

    }

    protected abstract void onUpdate(double tpf);

    /**
     * Called before exit.
     */
    protected void onExit() {

    }
}
