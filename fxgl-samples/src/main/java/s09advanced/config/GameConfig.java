/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced.config;

import com.almasb.fxgl.app.SetGameConfig;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetGameConfig
public class GameConfig {

    private String enemyName;
    private int numEnemies;
    private double lootRatio;
    private boolean killInventoryOnDeath;

    public String getEnemyName() {
        return enemyName;
    }

    public int getNumEnemies() {
        return numEnemies;
    }

    public double getLootRatio() {
        return lootRatio;
    }

    public boolean isKillInventoryOnDeath() {
        return killInventoryOnDeath;
    }
}
