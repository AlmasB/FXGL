/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.state;

import com.almasb.fxgl.core.fsm.StateMachine;
import com.almasb.fxgl.entity.component.Component;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Enables and manages finite state machine behavior of an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class StateComponent extends Component {

    private ReadOnlyObjectWrapper<EntityState> currentState;
    private StateMachine<EntityState> fsm;

    public StateComponent() {
        this(EntityState.IDLE);
    }

    public StateComponent(EntityState initialState) {
        currentState = new ReadOnlyObjectWrapper<>(initialState);
        fsm = new StateMachine<>(initialState);
    }

    @Override
    public void onUpdate(double tpf) {
        getCurrentState().onUpdate(tpf);
    }

    public ReadOnlyObjectProperty<EntityState> currentStateProperty() {
        return currentState.getReadOnlyProperty();
    }

    public EntityState getCurrentState() {
        return fsm.getCurrentState();
    }

    /**
     * @return true if the current state is EntityState.IDLE
     */
    public boolean isIdle() {
        return getCurrentState() == EntityState.IDLE;
    }

    /**
     * Changes current state to EntityState.IDLE.
     */
    public void changeStateToIdle() {
        changeState(EntityState.IDLE);
    }

    /**
     * Changes current state to given state only if current state != new state.
     */
    public void changeState(EntityState state) {
        if (getCurrentState() == state)
            return;

        fsm.changeState(state);
        currentState.setValue(state);
    }

    /**
     * Changes current state to given state regardless of current state.
     */
    public void changeStateAllowReentry(EntityState state) {
        fsm.changeState(state);
        currentState.setValue(state);
    }

    /**
     * @return true if the current state matches one of the given states
     */
    public boolean isIn(EntityState... states) {
        for (var state : states) {
            if (state == getCurrentState()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
