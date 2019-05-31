/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module com.almasb.fxgl.achievement {
    requires com.almasb.fxgl.core;
    requires com.almasb.fxgl.events;

    exports com.almasb.fxgl.achievement;

    opens com.almasb.fxgl.achievement to com.almasb.fxgl.core;
}