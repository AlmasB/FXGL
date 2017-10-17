/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Control extends Module {

    private boolean paused = false;

    public final boolean isPaused() {
        return paused;
    }

    public final void pause() {
        paused = true;
    }

    public final void resume() {
        paused = false;
    }

    public abstract void onUpdate(Entity entity, double tpf);
}
