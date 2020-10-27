/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 *
 */
module com.almasb.fxgl.controllerinput {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.input;

    exports com.almasb.fxgl.controllerinput;

    opens com.almasb.fxgl.controllerinput to com.almasb.fxgl.core;
}