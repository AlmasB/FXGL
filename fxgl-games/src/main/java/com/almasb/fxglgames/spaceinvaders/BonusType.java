/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum BonusType {
    ATTACK_RATE("powerup_atk_rate.png"), LIFE("life.png");

    final String textureName;

    BonusType(String textureName) {
        this.textureName = textureName;
    }
}
