/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai;

import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.ai.SingleAction;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AttackTask extends SingleAction {

    @Override
    public void onUpdate(double tpf) {
        Entity player = ((BehaviorSample) FXGL.getApp()).player;

        if (player.distance(getObject()) < 100) {
            getObject().getControl(AIControl.class).setBubbleMessage("Attack");
        } else {
            getObject().getControl(AIControl.class).setBubbleMessage("Chase");
            double speed = tpf * 60 * 5;

            Point2D vector = player.getPosition()
                    .subtract(getObject().getPosition())
                    .normalize()
                    .multiply(speed);

            getObject().getPositionComponent().translate(vector);
        }
    }
}
