/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module fxgl.notification {
    requires fxgl.core;
    requires fxgl.animation;
    requires fxgl.time;

    exports com.almasb.fxgl.notification;
    exports com.almasb.fxgl.notification.view;

    exports com.almasb.fxgl.notification.impl to fxgl.all;
    opens com.almasb.fxgl.notification.impl to fxgl.core;
}