/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
module fxgl.entity {
    requires fxgl.core;

    opens com.almasb.fxgl.entity.component to fxgl.core;
    opens com.almasb.fxgl.entity.components to fxgl.core;

    exports com.almasb.fxgl.entity;
    exports com.almasb.fxgl.entity.component;
    exports com.almasb.fxgl.entity.components;
    exports com.almasb.fxgl.entity.level;
    exports com.almasb.fxgl.physics;
    exports com.almasb.fxgl.physics.box2d.dynamics;
}