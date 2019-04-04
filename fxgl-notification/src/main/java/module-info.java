/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module fxgl.notification {
    requires fxgl.core;
    requires fxgl.animation;
    requires fxgl.time;

    exports com.almasb.fxgl.notification;

    provides com.almasb.fxgl.notification.NotificationService with com.almasb.fxgl.notification.impl.NotificationServiceProvider;
}