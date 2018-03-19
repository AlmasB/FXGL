/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.entity.Entity;

/**
 * TODO: interface?
 * on enter / exit?
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class FSMState {

    protected abstract void onUpdate(Entity entity, double tpf);
}
