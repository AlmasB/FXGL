/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.dynamics.Filter;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;

/**
 * Implement this class to provide collision filtering. In other words, you can implement
 * this class if you want finer control over contact creation.
 * @author Daniel Murphy
 */
public class ContactFilter {

    /**
     * @warning for performance reasons this is only called when the AABBs begin to overlap.
     * @param fixtureA first fixture
     * @param fixtureB second fixture
     * @return true if contact calculations should be performed between these two shapes
     */
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        Filter filterA = fixtureA.getFilterData();
        Filter filterB = fixtureB.getFilterData();

        if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0) {
            return filterA.groupIndex > 0;
        }

        return (filterA.maskBits & filterB.categoryBits) != 0 &&
                (filterA.categoryBits & filterB.maskBits) != 0;
    }
}
