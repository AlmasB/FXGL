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
}