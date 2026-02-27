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
    requires transitive com.almasb.fxgl.io;
    requires transitive com.almasb.fxgl.scene;

    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.media;

    requires java.desktop;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.gluonhq.attach.lifecycle;

    opens com.almasb.fxgl.achievement to com.almasb.fxgl.core;
    opens com.almasb.fxgl.minigames to com.almasb.fxgl.core;
    opens com.almasb.fxgl.quest to com.almasb.fxgl.core;
    opens com.almasb.fxgl.cutscene to com.almasb.fxgl.core;
    opens com.almasb.fxgl.cutscene.dialogue;
    opens com.almasb.fxgl.dsl to com.almasb.fxgl.core;
    opens com.almasb.fxgl.dsl.components to com.almasb.fxgl.core;
    opens com.almasb.fxgl.dev to com.almasb.fxgl.core;
    opens com.almasb.fxgl.dev.profiling to com.almasb.fxgl.core;
    opens com.almasb.fxgl.app to com.almasb.fxgl.core;
    opens com.almasb.fxgl.app.services to com.almasb.fxgl.core;
    opens com.almasb.fxgl.scene3d to com.almasb.fxgl.core;

    exports com.almasb.fxgl.app;
    exports com.almasb.fxgl.app.services;
    exports com.almasb.fxgl.app.scene;
    exports com.almasb.fxgl.dev;
    exports com.almasb.fxgl.dev.editor;
    exports com.almasb.fxgl.dsl;
    exports com.almasb.fxgl.dsl.components;
    exports com.almasb.fxgl.dsl.components.view;
    exports com.almasb.fxgl.dsl.effects;
    exports com.almasb.fxgl.dsl.handlers;
    exports com.almasb.fxgl.dsl.views;
    exports com.almasb.fxgl.gameplay;
    exports com.almasb.fxgl.multiplayer;
    exports com.almasb.fxgl.scene3d;
    exports com.almasb.fxgl.achievement;
    exports com.almasb.fxgl.cutscene;
    exports com.almasb.fxgl.cutscene.dialogue;
    exports com.almasb.fxgl.inventory;
    exports com.almasb.fxgl.minigames;
    exports com.almasb.fxgl.minigames.circuitbreaker;
    exports com.almasb.fxgl.minigames.sweetspot;
    exports com.almasb.fxgl.minigames.lockpicking;
    exports com.almasb.fxgl.minigames.randomoccurrence;
    exports com.almasb.fxgl.minigames.triggermash;
    exports com.almasb.fxgl.minigames.triggersequence;
    exports com.almasb.fxgl.quest;
    exports com.almasb.fxgl.trade;
    exports com.almasb.fxgl.trade.view;
}