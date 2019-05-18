/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.media {
    requires com.almasb.fxgl.core;
    requires javafx.media;

    exports com.almasb.fxgl.audio;
    exports com.almasb.fxgl.texture;

    exports com.almasb.fxgl.audio.impl to com.almasb.fxgl.all;

    opens com.almasb.fxgl.audio to com.almasb.fxgl.core;
}