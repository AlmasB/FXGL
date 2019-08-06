/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.minigames {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.animation;
    requires com.almasb.fxgl.input;
    requires javafx.controls;

    exports com.almasb.fxgl.minigames;
    exports com.almasb.fxgl.minigames.sweetspot;
    exports com.almasb.fxgl.minigames.lockpicking;
    exports com.almasb.fxgl.minigames.triggermash;
    exports com.almasb.fxgl.minigames.triggersequence;
}