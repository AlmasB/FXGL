/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class GeoWarsConfig {

    private double redEnemyChance;
    private int redEnemyHealth;
    private int redEnemyMoveSpeed;
    private int seekerMaxMoveSpeed;
    private int wandererMaxMoveSpeed;

    private int enemyHealth;

    public double getRedEnemyChance() {
        return redEnemyChance;
    }

    public int getRedEnemyHealth() {
        return redEnemyHealth;
    }

    public int getRedEnemyMoveSpeed() {
        return redEnemyMoveSpeed;
    }

    public int getSeekerMaxMoveSpeed() {
        return seekerMaxMoveSpeed;
    }

    public int getWandererMaxMoveSpeed() {
        return wandererMaxMoveSpeed;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }
}
