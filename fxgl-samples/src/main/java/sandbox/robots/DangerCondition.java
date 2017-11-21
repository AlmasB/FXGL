/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots;

import com.almasb.fxgl.ai.Condition;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DangerCondition extends Condition {
    @Override
    public boolean evaluate() {
        return getEntity().getWorld()
                .getEntitiesByType(MarioType.OBSTACLE)
                .stream()
                .anyMatch(e -> {
                    return !(getEntity().getX() > e.getRightX() || getEntity().getRightX() < e.getX());
                });
    }
}
