/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman.control;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum MoveDirection {
    UP, RIGHT, DOWN, LEFT;

    MoveDirection next() {
        int index = ordinal() + 1;

        if (index == values().length) {
            index = 0;
        }

        return values()[index];
    }

    MoveDirection prev() {
        int index = ordinal() - 1;

        if (index == -1) {
            index = values().length - 1;
        }

        return values()[index];
    }
}
