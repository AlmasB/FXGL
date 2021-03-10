/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.cutscene {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.input;
    requires com.almasb.fxgl.scene;

    requires com.fasterxml.jackson.annotation;

    exports com.almasb.fxgl.cutscene;
    exports com.almasb.fxgl.cutscene.dialogue;

    opens com.almasb.fxgl.cutscene to com.almasb.fxgl.core;
    opens com.almasb.fxgl.cutscene.dialogue;
}