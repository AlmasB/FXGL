/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai;

import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.ai.Action;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.GameEntity;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AttackTask extends Action {

    @Override
    public void action() {
        GameEntity player = ((BehaviorSample) FXGL.getApp()).player;

        if (player.distance(getObject()) < 100) {
            getObject().getControl(AIControl.class).setBubbleMessage("Attack");
        } else {
            getObject().getControl(AIControl.class).setBubbleMessage("Chase");
            double speed = 0.017 * 60 * 5;

            Point2D vector = player.getPosition()
                    .subtract(getObject().getPosition())
                    .normalize()
                    .multiply(speed);

            getObject().getPositionComponent().translate(vector);
        }
    }
}
