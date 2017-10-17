/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman.ai;

import com.almasb.fxgl.ai.Condition;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.pacman.PacmanApp;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerCloseCondition extends Condition {

    @Override
    public boolean evaluate() {
        Entity player = ((PacmanApp) FXGL.getApp()).getPlayer();

        return player.getPositionComponent().distance(getObject().getPositionComponent())
                < PacmanApp.MAP_SIZE * PacmanApp.BLOCK_SIZE / 3;
    }
}
