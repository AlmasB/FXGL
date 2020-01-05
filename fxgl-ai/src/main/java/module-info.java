/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 *
 */
module com.almasb.fxgl.ai {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.entity;

    exports com.almasb.fxgl.pathfinding;
    exports com.almasb.fxgl.pathfinding.astar;
    exports com.almasb.fxgl.pathfinding.maze;
    exports com.almasb.fxgl.procedural;

    opens com.almasb.fxgl.pathfinding.astar to com.almasb.fxgl.core;
}