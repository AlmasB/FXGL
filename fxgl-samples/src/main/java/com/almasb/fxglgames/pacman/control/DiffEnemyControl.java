/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxglgames.pacman.PacmanApp;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DiffEnemyControl extends EnemyControl {

    private PlayerControl playerControl;

    @Override
    protected MoveDirection updateMoveDirection() {

        if (playerControl == null) {
            playerControl = ((PacmanApp) FXGL.getApp()).getPlayerControl();
        }

        return playerControl.getMoveDirection().next().next();
    }
}
