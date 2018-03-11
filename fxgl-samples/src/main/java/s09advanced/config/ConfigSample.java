/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced.config;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;

/**
 * Shows how to use config files.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ConfigSample extends GameApplication {

    public static class GameConfig {

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

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("BasicAppSample");
        settings.setVersion("0.1");
        settings.setConfigClass(GameConfig.class);
    }

    @Override
    protected void initGame() {
        GameConfig config = getGameConfig();

        System.out.println("Enemy name: " + config.getEnemyName());
        System.out.println("Number of enemies: " + config.getNumEnemies());
        System.out.println("Loot ratio: " + config.getLootRatio());
        System.out.println("Destroy inventory on death: " + config.isKillInventoryOnDeath());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
