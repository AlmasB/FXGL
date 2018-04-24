/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiledtest;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
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

        return Entities.builder()
                .from(data)
                .type(EType.PLAYER)
                .bbox(new HitBox(BoundingShape.box(32, 42)))
                .with(physics, new MarioComponent())
                .with(new CollidableComponent(true))
                .build();
    }
}
