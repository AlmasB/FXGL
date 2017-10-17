/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.breakout.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.ViewComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BrickControl extends Control {

    private int lives = 2;

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void onHit() {
        FXGL.getAudioPlayer().playSound("breakout/brick_hit.wav");

        lives--;

        if (lives == 1) {
            ViewComponent view = getEntity().getComponent(ViewComponent.class);
            view.setView(FXGL.getAssetLoader().loadTexture("breakout/brick_blue_cracked.png", 232 / 3, 104 / 3));
        } else if (lives == 0) {
            getEntity().removeFromWorld();
        }
    }
}
