/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots;

import com.almasb.fxgl.entity.SetEntityFactory;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class MarioFactory implements EntityFactory {

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(MarioType.PLATFORM)
                .bbox(new HitBox("main", BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent(), new CollidableComponent(true))
                .build();
    }

    @Spawns("ghost_platform")
    public Entity newGhostPlatform(SpawnData data) {
        Entity platform =  Entities.builder()
                .from(data)
                .type(MarioType.GHOST_PLATFORM)
                .viewFromTexture("ghost_platform.png")
                .bbox(new HitBox("main", BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent(), new CollidableComponent(true))
                .build();

        platform.getView().setVisible(false);

        return platform;
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(MarioType.COIN)
                .viewFromTextureWithBBox("coin.png")
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().friction(0).density(0.25f));
        physics.setGenerateGroundSensor(true);

        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true);

        physics.setBodyDef(bd);
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .from(data)
                .type(MarioType.PLAYER)
                .bbox(new HitBox("main", BoundingShape.circle(15)))
                .bbox(new HitBox("lower", new Point2D(15 - 5, 30), BoundingShape.box(10, 10)))
                .with(physics, new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    @Spawns("robot")
    public Entity newRobot(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().friction(0).density(0.25f));

        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true);

        physics.setBodyDef(bd);
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .from(data)
                .type(MarioType.ROBOT)
                .bbox(new HitBox("main", new Point2D(275 / 2 - 105/2, 275 / 2 - 210/2), BoundingShape.box(105, 210)))
                .bbox(new HitBox("lower", new Point2D(275 / 2 - 15, 125*2), BoundingShape.box(30, 10)))
                .with(physics, new CollidableComponent(true))
                .with(new RobotControl())
                .build();
    }

    @Spawns("portal")
    public Entity newPortal(SpawnData data) {
        return Entities.builder()
                .type(MarioType.PORTAL)
                .from(data)
                .viewFromTextureWithBBox("finish.png")
                .with(new CollidableComponent(true))
                .build();
    }
}
