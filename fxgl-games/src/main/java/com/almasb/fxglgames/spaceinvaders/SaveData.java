/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders;

import java.io.Serializable;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int highScore;

    public SaveData(String name, int highScore) {
        this.name = name;
        this.highScore = highScore;
    }

    public String getName() {
        return name;
    }

    public int getHighScore() {
        return highScore;
    }
}
