/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class FSMControl extends Control {

    private FSMState state;

    public FSMControl(FSMState state) {
        this.state = state;
    }

    @Override
    public final void onUpdate(Entity entity, double tpf) {
        state.onUpdate(entity, tpf);
    }

    public final void setState(FSMState state) {
        this.state = state;
    }

    public final FSMState getState() {
        return state;
    }
}
