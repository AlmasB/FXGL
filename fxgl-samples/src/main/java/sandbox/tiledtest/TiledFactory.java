/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiledtest;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.ScriptComponent;
import com.almasb.fxgl.extra.entity.components.ActivatorComponent;
import com.almasb.fxgl.extra.entity.components.ExpireCleanComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.SensorCollisionHandler;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TiledFactory implements EntityFactory {

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(EType.PLATFORM)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("crate")
    public Entity newCrate(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(EType.CRATE)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent(), new CrateComponent(), new CollidableComponent(true))
                .build();
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(EType.COIN)
                .viewFromTexture("coin.png")
                //.bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                //.with(new PhysicsComponent(), new CrateComponent(), new CollidableComponent(true))
                .build();
    }

    @Spawns("lock")
    public Entity newLock(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(EType.LOCK)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new ScriptComponent(), new ActivatorComponent(), new CollidableComponent(true))
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        String subType = data.get("subType");

        switch (subType) {
            case "plant":
                return Entities.builder()
                        .from(data)
                        .type(EType.ENEMY)
                        .bbox(new HitBox(BoundingShape.box(70, 70)))
                        .viewFromAnimatedTexture("enemy_plant.png", 4, Duration.seconds(1.5))
                        .with(new CollidableComponent(true))
                        .build();
            default:
                throw new RuntimeException("TODO");
        }
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        physics.addSensor(new HitBox(BoundingShape.box(50, 10)), new SensorCollisionHandler() {
            @Override
            protected void onCollisionBegin(Entity other) {
                System.out.println("begin");
            }

            @Override
            protected void onCollision(Entity other) {
                System.out.println("collision");
            }

            @Override
            protected void onCollisionEnd(Entity other) {
                System.out.println("end");
            }
        });

        return Entities.builder()
                .from(data)
                .type(EType.PLAYER)
                .bbox(new HitBox(BoundingShape.box(32, 42)))
                .with(physics, new MarioComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().density(0.05f));
        physics.setBodyType(BodyType.DYNAMIC);

        physics.setOnPhysicsInitialized(() -> {
            Point2D mousePosition = FXGL.getInput().getMousePositionWorld();

            physics.setLinearVelocity(mousePosition.subtract(data.getX(), data.getY()).normalize().multiply(800));
        });

        return Entities.builder()
                .type(EType.BULLET)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(25, 25, Color.BLUE))
                .with(physics, new CollidableComponent(true))
                .with(new ExpireCleanComponent(Duration.seconds(1)))
                .build();
    }

    @Spawns("polygon")
    public Entity newPolygon(SpawnData data) {

        Polygon polygon = data.get("polygon");

        return Entities.builder()
                .type(EType.PLATFORM)
                .from(data)
                .bbox(new HitBox(BoundingShape.polygonFromDoubles(polygon.getPoints())))
                .viewFromNode(polygon)
                .with(new PhysicsComponent())
                .build();
    }
}
