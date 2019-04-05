/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module fxgl.all {
    requires transitive fxgl.core;
    requires transitive fxgl.animation;
    requires transitive fxgl.effects;
    requires transitive fxgl.entity;
    requires transitive fxgl.events;
    requires transitive fxgl.input;
    requires transitive fxgl.io;
    requires transitive fxgl.media;
    requires transitive fxgl.notification;
    requires transitive fxgl.time;
    requires transitive fxgl.ui;

    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.swing;

    uses com.almasb.fxgl.notification.NotificationService;
    uses com.almasb.fxgl.core.EngineService;

    opens com.almasb.fxgl.dsl to fxgl.core;
    opens com.almasb.fxgl.dev to fxgl.core;

    exports com.almasb.fxgl.app;
    exports com.almasb.fxgl.dsl;
    exports com.almasb.fxgl.dsl.components;
    exports com.almasb.fxgl.gameplay;
    exports com.almasb.fxgl.saving;
    exports com.almasb.fxgl.scene;
}