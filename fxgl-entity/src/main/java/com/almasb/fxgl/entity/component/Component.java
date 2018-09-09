/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.entity.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Component {

    protected Entity entity;

    private boolean paused = false;

    public final Entity getEntity() {
        return entity;
    }

    final void setEntity(Entity entity) {
        this.entity = entity;
    }

    public final boolean isPaused() {
        return paused;
    }

    public final void pause() {
        paused = true;
    }

    public final void resume() {
        paused = false;
    }

    /**
     * Called after the component is added to entity.
     */
    public void onAdded() {

    }

    /**
     * Called each frame.
     *
     * @param tpf time per frame
     */
    public void onUpdate(double tpf) {

    }

    /**
     * Called before the component is removed from entity.
     */
    public void onRemoved() {

    }

    @Override
    public String toString() {
        String simpleName = getClass().getSimpleName();

        int index = simpleName.indexOf("Component");

        if (index != -1) {
            simpleName = simpleName.substring(0, index);
        }

        return simpleName + "()";
    }
}
