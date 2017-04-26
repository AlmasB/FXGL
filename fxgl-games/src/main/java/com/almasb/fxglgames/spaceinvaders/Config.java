/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
