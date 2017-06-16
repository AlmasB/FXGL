/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.worlds;

import com.almasb.fxgl.physics.box2d.dynamics.World;

public interface PerformanceTestWorld {
    void setupWorld(World world);

    void step();
}
