/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection.grid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NeighborFilteringOptionTest {

    @Test
    public void testIs() {
        assertTrue(NeighborFilteringOption.FOUR_DIRECTIONS.is(NeighborFilteringOption.FOUR_DIRECTIONS));
        assertFalse(NeighborFilteringOption.EIGHT_DIRECTIONS.is(NeighborFilteringOption.FOUR_DIRECTIONS));
    }

}