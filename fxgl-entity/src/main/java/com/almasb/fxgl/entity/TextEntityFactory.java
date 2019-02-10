/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

/**
 * Marks a class that it's able to spawn entities from text-based levels.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface TextEntityFactory extends EntityFactory {

    /**
     * @return character that will be ignored within the text level
     */
    char emptyChar();

    /**
     * @return width of a single text character
     */
    int blockWidth();

    /**
     * @return height of a single text character
     */
    int blockHeight();
}
