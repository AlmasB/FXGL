/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.entity {
    requires com.almasb.fxgl.core;
    requires java.xml;

    exports com.almasb.fxgl.ai.senseai;
    exports com.almasb.fxgl.ai.goap;
    exports com.almasb.fxgl.entity;
    exports com.almasb.fxgl.entity.action;
    exports com.almasb.fxgl.entity.component;
    exports com.almasb.fxgl.entity.components;
    exports com.almasb.fxgl.entity.level;
    exports com.almasb.fxgl.entity.level.text;
    exports com.almasb.fxgl.entity.level.tiled;
    exports com.almasb.fxgl.entity.state;
    exports com.almasb.fxgl.particle;
    exports com.almasb.fxgl.pathfinding;
    exports com.almasb.fxgl.pathfinding.astar;
    exports com.almasb.fxgl.pathfinding.dfs;
    exports com.almasb.fxgl.pathfinding.dungeon;
    exports com.almasb.fxgl.pathfinding.heuristic;
    exports com.almasb.fxgl.pathfinding.maze;
    exports com.almasb.fxgl.physics;
    exports com.almasb.fxgl.physics.box2d.dynamics;
    exports com.almasb.fxgl.physics.box2d.dynamics.joints;
    exports com.almasb.fxgl.procedural;

    opens com.almasb.fxgl.entity.component to com.almasb.fxgl.core;
    opens com.almasb.fxgl.entity.components to com.almasb.fxgl.core;
    opens com.almasb.fxgl.pathfinding.astar to com.almasb.fxgl.core;
    opens com.almasb.fxgl.pathfinding to com.almasb.fxgl.core;
}