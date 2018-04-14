/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots;

import com.almasb.fxgl.ai.SingleAction;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class IdleAction extends SingleAction {
    @Override
    public void onUpdate(double tpf) {
        getEntity().getComponent(PlayerControl.class).stop();
    }
}
