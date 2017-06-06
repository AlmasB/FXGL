/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.physics.box2d.dynamics.contacts.Position;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Velocity;

public class SolverData {
    public TimeStep step;
    public Position[] positions;
    public Velocity[] velocities;
}
