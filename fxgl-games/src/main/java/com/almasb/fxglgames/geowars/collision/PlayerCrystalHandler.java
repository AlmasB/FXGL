/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.geowars.GeoWarsType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class PlayerCrystalHandler extends CollisionHandler {

    public PlayerCrystalHandler() {
        super(GeoWarsType.PLAYER, GeoWarsType.CRYSTAL);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity crystal) {
        crystal.removeFromWorld();

        FXGL.getApp().getGameState().increment("multiplier", +1);
    }
}
