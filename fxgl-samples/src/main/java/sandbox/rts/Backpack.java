/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Backpack {

    private int gold = 0;

    public int getGold() {
        return gold;
    }

    public void addGold(int value) {
        gold += value;
    }
}
