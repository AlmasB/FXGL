/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.TypeComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyBatControl extends BatControl {
    private Entity ball;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        super.onUpdate(entity, tpf);

        if (ball == null) {
            for (Entity e : entity.getWorld().getEntitiesByComponent(TypeComponent.class)) {
                if (e.getComponent(TypeComponent.class).isType(EntityType.BALL)) {
                    ball = e;
                    break;
                }
            }
        } else {
            moveAI();
        }
    }

    private void moveAI() {
        BoundingBoxComponent ballBox = ball.getComponent(BoundingBoxComponent.class);
        BoundingBoxComponent batBox = getEntity().getComponent(BoundingBoxComponent.class);

        boolean isBallToLeft = ballBox.getMaxXWorld() <= batBox.getMinXWorld();

        if (ballBox.getMinYWorld() < batBox.getMinYWorld()) {
            if (isBallToLeft)
                up();
            else
                down();
        } else if (ballBox.getMinYWorld() > batBox.getMinYWorld()) {
            if (isBallToLeft)
                down();
            else
                up();
        } else {
            stop();
        }
    }
}
