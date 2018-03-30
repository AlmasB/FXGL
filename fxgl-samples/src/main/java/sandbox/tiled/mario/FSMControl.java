/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class FSMControl extends Component {

    private FSMState state;

    public FSMControl(FSMState state) {
        this.state = state;
    }

    public FSMControl() {
    }

    @Override
    public final void onUpdate(double tpf) {
        state.onUpdate(tpf);
    }

    public final void setState(FSMState state) {
        this.state = state;
    }

    public final FSMState getState() {
        return state;
    }
}
