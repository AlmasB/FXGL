/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection.grid;


/**
 * Define Movement Directions
 *
 * @author Jean-Rene Lavoie (jeanrlavoie@gmail.com)
 */
public enum NeighborFilteringOption {

    FOUR_DIRECTIONS, EIGHT_DIRECTIONS;

    public boolean is(NeighborFilteringOption... neighborFilteringOptions) {
        for(NeighborFilteringOption neighborFilteringOption : neighborFilteringOptions) {
            if(neighborFilteringOption.equals(this)) {
                return true;
            }
        }
        return false;
    }

}
