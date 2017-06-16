/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GoldMine implements Resource {

    private static final int MAX_GATHERERS = 2;

    private int gatherers = 0;

    public boolean isFull() {
        return gatherers == MAX_GATHERERS;
    }

    @Override
    public void onStartGathering() {
        gatherers++;
    }

    @Override
    public void onEndGathering() {
        gatherers--;
    }
}
