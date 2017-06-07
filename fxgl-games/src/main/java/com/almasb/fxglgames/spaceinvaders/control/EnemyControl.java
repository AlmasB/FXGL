/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.ecs.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {

    private LocalTimer attackTimer;
    private LocalTimer moveTimer;
    private Duration nextAttack = Duration.seconds(2);
    private Duration nextMove = Duration.seconds(2);

    private boolean moveRight = true;

    private PositionComponent position;
    private BoundingBoxComponent bbox;

    @Override
    public void onAdded(Entity entity) {
        attackTimer = FXGL.newLocalTimer();
        attackTimer.capture();

        moveTimer = FXGL.newLocalTimer();
        moveTimer.capture();

        bbox = Entities.getBBox(entity);
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (attackTimer.elapsed(nextAttack)) {
            if (FXGLMath.randomBoolean(0.3f)) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * Math.random());
            attackTimer.capture();
        }

        if (moveTimer.elapsed(nextMove)) {
            moveRight = !moveRight;
            moveTimer.capture();
        }

        if (bbox.getMaxYWorld() >= FXGL.getApp().getHeight()) {
            FXGL.getEventBus().fireEvent(new GameEvent(GameEvent.ENEMY_REACHED_END));
            getEntity().removeFromWorld();
        }

        double speed = tpf * 90 * (moveRight ? 1 : -1);

        position.translateX(speed);
        position.translateY(tpf * 5);
    }

    private void shoot() {
        GameWorld world = (GameWorld) getEntity().getWorld();
        world.spawn("Bullet", new SpawnData(0, 0).put("owner", getEntity()));

        FXGL.getAudioPlayer().playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }
}
