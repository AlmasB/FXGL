/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.state;

import com.almasb.fxgl.core.fsm.State;

/**
 * A state in which an entity can be in (via StateComponent).
 * 
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EntityState implements State<EntityState> {

    public static final EntityState IDLE = new EntityState("DEFAULT_IDLE");

    private String name;

    public EntityState(String name) {
        this.name = name;
    }

    public EntityState() {
        this("");
    }

    @Override
    public final boolean isAllowConcurrency() {
        return false;
    }

    @Override
    public final boolean isSubState() {
        return false;
    }

    @Override
    public final void onCreate() {
        onEntering();
    }

    @Override
    public final void onDestroy() {
        onExited();
    }

    /**
     * Called before entering to this state.
     */
    public void onEntering() { }

    @Override
    public void onEnteredFrom(EntityState entityState) { }

    @Override
    public void onExitingTo(EntityState entityState) { }

    /**
     * Called after exiting this state.
     */
    public void onExited() { }

    protected void onUpdate(double tpf) { }

    @Override
    public String toString() {
        return name.isEmpty() ? super.toString() : name;
    }
}
