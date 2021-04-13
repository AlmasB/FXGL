/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 *
 */
module com.almasb.fxgl.ui {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.input;
    requires com.almasb.fxgl.localization;
    requires com.almasb.fxgl.scene;

    requires javafx.controls;

    exports com.almasb.fxgl.notification;
    exports com.almasb.fxgl.notification.view;
    exports com.almasb.fxgl.ui;

    exports com.almasb.fxgl.notification.impl to com.almasb.fxgl.all;

    opens com.almasb.fxgl.notification.impl to com.almasb.fxgl.core;
    opens com.almasb.fxgl.ui to com.almasb.fxgl.core;
}