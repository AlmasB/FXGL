/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spacerunner.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.spacerunner.SpaceRunnerFactory;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyControl extends Control {

    private LocalTimer attackTimer;
    private Duration nextAttack = Duration.seconds(2);

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {

        attackTimer = FXGL.newLocalTimer();
        attackTimer.capture();

        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

        if (attackTimer.elapsed(nextAttack)) {
            if (Math.random() < 0.8) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * Math.random());
            attackTimer.capture();
        }

        position.translateX(tpf * 30);
    }

    private void shoot() {
        // TODO: refactor
//        Entity bullet = FXGL.getInstance(SpaceRunnerFactory.class)
//                .newBullet(position.getX(), position.getY() + 20, SpaceRunnerType.ENEMY);
//
//        getEntity().getWorld().addEntity(bullet);

        //audioPlayer.playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }
}

