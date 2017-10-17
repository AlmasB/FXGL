/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class PlayerEnemyHandler extends CollisionHandler {

    private PacmanApp app;

    public PlayerEnemyHandler() {
        super(PacmanType.PLAYER, PacmanType.ENEMY);

        app = (PacmanApp) FXGL.getApp();
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity enemy) {

        app.onPlayerKilled();
    }
}
