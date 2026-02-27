/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 *
 */
module com.almasb.fxgl.core {
    requires transitive kotlin.stdlib;
    requires transitive javafx.graphics;
    requires transitive javafx.base;

    requires java.desktop;
    requires java.xml;
    requires javafx.media;
    requires com.gluonhq.attach.audio;

    exports com.almasb.fxgl.core;
    exports com.almasb.fxgl.core.asset;
    exports com.almasb.fxgl.core.collection;
    exports com.almasb.fxgl.core.collection.grid;
    exports com.almasb.fxgl.core.concurrent;
    exports com.almasb.fxgl.core.fsm;
    exports com.almasb.fxgl.core.math;
    exports com.almasb.fxgl.core.pool;
    exports com.almasb.fxgl.core.reflect;
    exports com.almasb.fxgl.core.serialization;
    exports com.almasb.fxgl.core.util;

    exports com.almasb.fxgl.animation;
    exports com.almasb.fxgl.audio;
    exports com.almasb.fxgl.event;
    exports com.almasb.fxgl.input;
    exports com.almasb.fxgl.input.view;
    exports com.almasb.fxgl.input.virtual;
    exports com.almasb.fxgl.localization;
    exports com.almasb.fxgl.logging;
    exports com.almasb.fxgl.texture;
    exports com.almasb.fxgl.time;

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