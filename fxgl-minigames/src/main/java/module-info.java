/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.minigames {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.animation;
    requires com.almasb.fxgl.input;
    requires com.almasb.fxgl.scene;
    requires javafx.controls;

    exports com.almasb.fxgl.minigames;
    exports com.almasb.fxgl.minigames.circuitbreaker;
    exports com.almasb.fxgl.minigames.sweetspot;
    exports com.almasb.fxgl.minigames.lockpicking;
    exports com.almasb.fxgl.minigames.triggermash;
    exports com.almasb.fxgl.minigames.triggersequence;

    opens com.almasb.fxgl.minigames to com.almasb.fxgl.core;
}