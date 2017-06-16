/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.dynamics.joints.Joint;

/**
 * Joints and fixtures are destroyed when their associated
 * body is destroyed. Implement this listener so that you
 * may nullify references to these joints and shapes.
 *
 * @author Daniel Murphy
 */
public interface DestructionListener {

    /**
     * Called when any joint is about to be destroyed due
     * to the destruction of one of its attached bodies.
     *
     * @param joint the joint to be destroyed
     */
    void onDestroy(Joint joint);

    /**
     * Called when any fixture is about to be destroyed due
     * to the destruction of its parent body.
     *
     * @param fixture the fixture to be destroyed
     */
    void onDestroy(Fixture fixture);
}
