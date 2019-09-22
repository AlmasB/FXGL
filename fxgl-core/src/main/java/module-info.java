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
    requires transitive sslogger.main;

    exports com.almasb.fxgl.core;
    exports com.almasb.fxgl.core.collection;
    exports com.almasb.fxgl.core.concurrent;
    exports com.almasb.fxgl.core.fsm;
    exports com.almasb.fxgl.core.math;
    exports com.almasb.fxgl.core.pool;
    exports com.almasb.fxgl.core.reflect;
    exports com.almasb.fxgl.core.serialization;
    exports com.almasb.fxgl.core.util;
}