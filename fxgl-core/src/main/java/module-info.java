/**
 *
 */
module fxgl.core {
    requires kotlin.stdlib;
    requires transitive javafx.graphics;
    requires transitive sslogger.main;

    exports com.almasb.fxgl.core;
    exports com.almasb.fxgl.core.collection;
    exports com.almasb.fxgl.core.concurrent;
    exports com.almasb.fxgl.core.math;
    exports com.almasb.fxgl.core.pool;
    exports com.almasb.fxgl.core.reflect;
    exports com.almasb.fxgl.core.serialization;
    exports com.almasb.fxgl.core.util;
    exports com.almasb.fxgl.core.local;
}