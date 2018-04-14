/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.extra.ai.fsm.DefaultStateMachine;
import com.almasb.fxgl.extra.ai.fsm.State;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FSMControl<T extends Entity> extends Component {

    private DefaultStateMachine<T, State<T>> fsm;

    public <S extends State<T>> FSMControl(Class<S> stateType, S initialState) {
        fsm = new DefaultStateMachine<>(null, initialState);
    }

    @Override
    public void onAdded() {
        fsm.setOwner((T) entity);
    }

    @Override
    public void onUpdate(double tpf) {
        fsm.update();
    }

    public void changeState(State<T> newState) {
        fsm.changeState(newState);
    }
}
