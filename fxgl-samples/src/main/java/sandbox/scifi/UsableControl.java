/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.scifi;

import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class UsableControl extends Component {

    private Runnable action;

    public UsableControl(Runnable action) {
        this.action = action;
    }

    @Override
    public void onUpdate(double tpf) {

    }

    public void use() {
        action.run();
    }
}
