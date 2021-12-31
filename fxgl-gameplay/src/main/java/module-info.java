/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.gameplay {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.scene;
    requires javafx.controls;
    requires com.fasterxml.jackson.annotation;

    exports com.almasb.fxgl.achievement;
    exports com.almasb.fxgl.cutscene;
    exports com.almasb.fxgl.cutscene.dialogue;
    exports com.almasb.fxgl.inventory;
    exports com.almasb.fxgl.inventory.view;
    exports com.almasb.fxgl.minigames;
    exports com.almasb.fxgl.minigames.circuitbreaker;
    exports com.almasb.fxgl.minigames.sweetspot;
    exports com.almasb.fxgl.minigames.lockpicking;
    exports com.almasb.fxgl.minigames.randomoccurrence;
    exports com.almasb.fxgl.minigames.triggermash;
    exports com.almasb.fxgl.minigames.triggersequence;
    exports com.almasb.fxgl.quest;
    exports com.almasb.fxgl.trade;
    exports com.almasb.fxgl.trade.view;

    opens com.almasb.fxgl.achievement to com.almasb.fxgl.core;
    opens com.almasb.fxgl.minigames to com.almasb.fxgl.core;
    opens com.almasb.fxgl.quest to com.almasb.fxgl.core;
    opens com.almasb.fxgl.cutscene to com.almasb.fxgl.core;
    opens com.almasb.fxgl.cutscene.dialogue;
}