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
    requires com.almasb.fxgl.media;
    requires java.xml;

    opens com.almasb.fxgl.entity.component to com.almasb.fxgl.core;
    opens com.almasb.fxgl.entity.components to com.almasb.fxgl.core;

    exports com.almasb.fxgl.entity;
    exports com.almasb.fxgl.entity.component;
    exports com.almasb.fxgl.entity.components;
    exports com.almasb.fxgl.entity.level;
    exports com.almasb.fxgl.entity.level.text;
    exports com.almasb.fxgl.entity.level.tiled;
    exports com.almasb.fxgl.physics;
    exports com.almasb.fxgl.physics.box2d.dynamics;
}