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

    public void changeStateToIdle() {
        changeState(EntityState.IDLE);
    }

    public void changeState(EntityState state) {
        fsm.changeState(state);
        currentState.setValue(state);
    }

    public boolean isIn(EntityState... states) {
        for (var state : states) {
            if (state == getCurrentState()) {
                return true;
            }
        }

        return false;
    }
}
