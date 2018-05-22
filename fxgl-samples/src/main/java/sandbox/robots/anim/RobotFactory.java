/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots.anim;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.extra.entity.components.OffscreenCleanComponent;
import com.almasb.fxgl.extra.entity.components.ProjectileComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotFactory implements EntityFactory {

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        Rectangle r = new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"));
        r.setArcWidth(25);
        r.setArcHeight(25);

        Entity e = Entities.builder()
                .from(data)
                .bbox(new HitBox("main", BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .viewFromNode(r)
                .with(new PhysicsComponent(), new CollidableComponent(true))
                .build();



        return e;
    }

    @Spawns("robot")
    public Entity newRobot(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().friction(0).density(0.25f));
        //physics.setRaycastIgnored(true);

        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true);

        physics.addGroundSensor(new HitBox("ground", new Point2D(275 / 2 - 40/2, 275 / 2 - 210/2 + 230 - 5/2), BoundingShape.box(40, 5)));
        physics.setBodyDef(bd);
        physics.setBodyType(BodyType.DYNAMIC);

        Entity e = Entities.builder()
                .from(data)
                .bbox(new HitBox("main", new Point2D(275 / 2 - 65/2, 275 / 2 - 210/2), BoundingShape.box(65, 230)))
                //.bbox(new HitBox("lower", new Point2D(275 / 2 - 15, 125*2), BoundingShape.box(30, 10)))
                .with(physics, new CollidableComponent(true))
                .with(new RobotComponent())
                .build();

        e.setScaleX(0);
        e.setScaleY(0);

        Entities.animationBuilder()
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .delay(Duration.seconds(0.25))
                .duration(Duration.seconds(1))
                .scale(e)
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .buildAndPlay();

        return e;
    }

    @Spawns("rocket")
    public Entity newRocket(SpawnData data) {
        return Entities.builder()
                .from(data)
                .viewFromNodeWithBBox(DSLKt.texture("rocket.png", 73, 20))
                .with(new OffscreenCleanComponent(), new ProjectileComponent(new Point2D(1, 0), 800))
                .build();
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        return Entities.builder()
                .from(data)
                .viewFromTextureWithBBox("coin.png")
                .with(new PhysicsComponent())
                .build();
    }
}
