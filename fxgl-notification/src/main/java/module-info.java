/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module fxgl.notification {
    requires fxgl.core;
    requires fxgl.animation;
    requires fxgl.time;
    requires fxgl.ui;

    exports com.almasb.fxgl.notification;

    opens com.almasb.fxgl.notification.impl to fxgl.core;

    provides com.almasb.fxgl.core.EngineService with com.almasb.fxgl.notification.impl.NotificationServiceProvider;
}