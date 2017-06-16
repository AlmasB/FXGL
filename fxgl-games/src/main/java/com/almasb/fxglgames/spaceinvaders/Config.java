/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Config {
    private Config() {}

    public static final String SAVE_DATA_NAME = "./hiscore.dat";

    public static final int WIDTH = 650;
    public static final int HEIGHT = 800;

    /**
     * Seconds to show level info.
     */
    public static final double LEVEL_START_DELAY = 2.4;

    public static final int SCORE_ENEMY_KILL = 250;
    public static final int SCORE_DIFFICULTY_MODIFIER = 1;

    public static final int START_LIVES = 3;

    public static final int ENEMIES_PER_ROW = 8;
    public static final int ENEMY_ROWS = 4;
    public static final int ENEMIES_PER_LEVEL = ENEMIES_PER_ROW * ENEMY_ROWS;

    public static final double BONUS_SPAWN_CHANCE = 0.25;
    public static final int BONUS_MOVE_SPEED = 150;

    public static final double PLAYER_MOVE_SPEED = 300;

    /**
     * Attack speed, bullet per second.
     */
    public static final double PLAYER_ATTACK_SPEED = 2.0;
    public static final double PLAYER_BONUS_ATTACK_SPEED = 0.25;

    public static final double INVINCIBILITY_TIME = 1.0;

    public static final int ACHIEVEMENT_ENEMIES_KILLED = 40;
    public static final int ACHIEVEMENT_MASTER_SCORER = 1000;

    public static final class Asset {
        public static final String SOUND_LOSE_LIFE = "lose_life.wav";
        public static final String SOUND_NEW_LEVEL = "level.wav";

        public static final String DIALOG_MOVE_LEFT = "move_left.mp3";
        public static final String DIALOG_MOVE_RIGHT = "move_right.mp3";
        public static final String DIALOG_SHOOT = "shoot.mp3";

        public static final String FXML_MAIN_UI = "main.fxml";
    }
}
