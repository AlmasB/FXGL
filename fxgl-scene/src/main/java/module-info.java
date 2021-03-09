/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.scene {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.input;

    exports com.almasb.fxgl.scene;

    opens com.almasb.fxgl.scene to com.almasb.fxgl.core;
}