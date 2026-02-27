/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 *
 */
module com.almasb.fxgl.io {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.scene;
    requires com.gluonhq.attach.storage;
    requires java.net.http;

    exports com.almasb.fxgl.io;
    exports com.almasb.fxgl.net;
    exports com.almasb.fxgl.profile;

    opens com.almasb.fxgl.io to com.almasb.fxgl.core;
    opens com.almasb.fxgl.profile to com.almasb.fxgl.core;
}