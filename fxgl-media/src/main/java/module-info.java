/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module fxgl.media {
    requires fxgl.core;
    requires javafx.media;

    exports com.almasb.fxgl.audio;
    exports com.almasb.fxgl.texture;

    exports com.almasb.fxgl.audio.impl to fxgl.all;
}