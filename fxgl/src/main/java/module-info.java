/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.all {
    requires transitive com.almasb.fxgl.core;
    requires transitive com.almasb.fxgl.achievement;
    requires transitive com.almasb.fxgl.ai;
    requires transitive com.almasb.fxgl.animation;
    requires transitive com.almasb.fxgl.cutscene;
    requires transitive com.almasb.fxgl.effects;
    requires transitive com.almasb.fxgl.entity;
    requires transitive com.almasb.fxgl.events;
    requires transitive com.almasb.fxgl.input;
    requires transitive com.almasb.fxgl.io;
    requires transitive com.almasb.fxgl.localization;
    requires transitive com.almasb.fxgl.media;
    requires transitive com.almasb.fxgl.minigames;
    requires transitive com.almasb.fxgl.notification;
    requires transitive com.almasb.fxgl.scene;
    requires transitive com.almasb.fxgl.time;
    requires transitive com.almasb.fxgl.ui;

    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.swing;

    opens com.almasb.fxgl.dsl to com.almasb.fxgl.core;
    opens com.almasb.fxgl.dev to com.almasb.fxgl.core;

    exports com.almasb.fxgl.app;
    exports com.almasb.fxgl.dev;
    exports com.almasb.fxgl.dsl;
    exports com.almasb.fxgl.dsl.components;
    exports com.almasb.fxgl.dsl.components.view;
    exports com.almasb.fxgl.dsl.effects;
    exports com.almasb.fxgl.dsl.handlers;
    exports com.almasb.fxgl.dsl.views;
    exports com.almasb.fxgl.gameplay;
    exports com.almasb.fxgl.saving;
}