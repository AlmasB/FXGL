/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.media {
    requires com.almasb.fxgl.core;
    requires javafx.media;

    requires com.gluonhq.attach.audio;

    exports com.almasb.fxgl.audio;
    exports com.almasb.fxgl.texture;

    exports com.almasb.fxgl.audio.impl;

    opens com.almasb.fxgl.audio to com.almasb.fxgl.core;
}