/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.notification {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.animation;
    requires com.almasb.fxgl.time;

    exports com.almasb.fxgl.notification;
    exports com.almasb.fxgl.notification.view;

    exports com.almasb.fxgl.notification.impl to com.almasb.fxgl.all;
    opens com.almasb.fxgl.notification.impl to com.almasb.fxgl.core;
}