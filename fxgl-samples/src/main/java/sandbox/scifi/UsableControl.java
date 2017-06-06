/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.scifi;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class UsableControl extends AbstractControl {

    private Runnable action;

    public UsableControl(Runnable action) {
        this.action = action;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void use() {
        action.run();
    }
}
