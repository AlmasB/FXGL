/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SeekerControl extends AbstractControl {

    // in seconds
    private static double seekerAdjustDelay = 5;

    // TODO: use Vec2 to avoid GC
    private Point2D velocity = Point2D.ZERO;
    private GameEntity player;
    private GameEntity seeker;

    private LocalTimer adjustDirectionTimer = FXGL.newLocalTimer();
    private Duration adjustDelay = Duration.seconds(seekerAdjustDelay);

    private int moveSpeed;

    public SeekerControl(GameEntity player, int moveSpeed) {
        this.player = player;
        this.moveSpeed = moveSpeed;

        if (seekerAdjustDelay > 0) {
            seekerAdjustDelay -= 0.15;
            if (seekerAdjustDelay < 0) {
                seekerAdjustDelay = 0;
            }
        }
    }

    @Override
    public void onAdded(Entity entity) {
        seeker = (GameEntity) entity;
        adjustVelocity(0.016);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        move(tpf);
        rotate();
    }

    private void move(double tpf) {
        if (adjustDirectionTimer.elapsed(adjustDelay)) {
            adjustVelocity(tpf);
            adjustDirectionTimer.capture();
        }

        seeker.translate(velocity);
    }

    private void adjustVelocity(double tpf) {
        Point2D directionToPlayer = player.getCenter()
                .subtract(seeker.getCenter())
                .normalize()
                .multiply(moveSpeed);

        velocity = velocity.add(directionToPlayer).multiply(tpf);
    }

    private void rotate() {
        if (!velocity.equals(Point2D.ZERO)) {
            seeker.rotateToVector(velocity);
        }
    }
}
