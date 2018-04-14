/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.state;

import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class StateComponent extends Component {

    private boolean allowStateReentrance = false;
    private State state;

    public StateComponent(State state) {
        this.state = state;
    }

    public StateComponent() {
    }

    public boolean isAllowStateReentrance() {
        return allowStateReentrance;
    }

    public void setAllowStateReentrance(boolean allowStateReentrance) {
        this.allowStateReentrance = allowStateReentrance;
    }

    /**
     * Called before state update.
     */
    protected void preUpdate(double tpf) {

    }

    @Override
    public final void onUpdate(double tpf) {
        preUpdate(tpf);
        state.onUpdate(tpf);
    }

    public final void setState(State state) {
        if (!allowStateReentrance && getState() == state) {
            return;
        }

        State prevState = this.state;

        if (prevState != null) {
            prevState.onExit();
        }

        this.state = state;

        this.state.onEnter(prevState);
    }

    public final State getState() {
        return state;
    }
}
