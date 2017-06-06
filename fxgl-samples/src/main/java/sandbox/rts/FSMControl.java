/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ai.fsm.DefaultStateMachine;
import com.almasb.fxgl.ai.fsm.State;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FSMControl<T extends Entity> extends AbstractControl {

    private DefaultStateMachine<T, State<T>> fsm;

    public <S extends State<T>> FSMControl(Class<S> stateType, S initialState) {
        fsm = new DefaultStateMachine<>(null, initialState);
    }

    @Override
    public void onAdded(Entity entity) {
        fsm.setOwner((T) entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        fsm.update();
    }

    public void changeState(State<T> newState) {
        fsm.changeState(newState);
    }
}
