/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module fxgl.all {
    requires kotlin.stdlib;
    requires fxgl.core;
    requires fxgl.animation;
    requires fxgl.effects;
    requires fxgl.entity;
    requires fxgl.events;
    requires fxgl.input;
    requires fxgl.io;
    requires fxgl.media;
    requires fxgl.time;
    requires fxgl.ui;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens com.almasb.fxgl.dsl to fxgl.core;

    exports com.almasb.fxgl.app;
    exports com.almasb.fxgl.dsl;
    exports com.almasb.fxgl.gameplay;
    exports com.almasb.fxgl.saving;
    exports com.almasb.fxgl.scene;
}