/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots;

import com.almasb.fxgl.ai.GoalAction;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveLeftAction extends GoalAction {

    private double startX;

    @Override
    public void start() {
        startX = getEntity().getX();
    }

    @Override
    public void onUpdate(double tpf) {
        getEntity().getComponent(PlayerControl.class).left();
    }

    @Override
    public boolean reachedGoal() {
        return startX - getEntity().getX() > 100;
    }
}
