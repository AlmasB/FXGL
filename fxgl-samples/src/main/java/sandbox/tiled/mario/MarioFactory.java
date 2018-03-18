/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.EffectControl;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class MarioFactory implements EntityFactory {

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .type(MarioType.ENEMY)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(30, 30, Color.RED))
                .with(physics)
                .with(new EnemyControl())
                .build();
    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return Entities.builder()
                .type(MarioType.PLATFORM)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("door")
    public Entity newDoor(SpawnData data) {
        return Entities.builder()
                .type(MarioType.DOOR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .type(MarioType.PLAYER)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(32, 42)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new PlayerControl(), new EffectControl())
                .build();
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        return Entities.builder()
                .type(MarioType.COIN)
                .from(data)
                .viewFromNodeWithBBox(new Circle(data.<Integer>get("width") / 2, Color.GOLD))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("block")
    public Entity newBlock(SpawnData data) {
        return Entities.builder()
                .type(MarioType.CRUSHER_BLOCK)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(70, 70))
                .with(new CollidableComponent(true))
                .with(new CrusherControl())
                .build();
    }

    @Spawns("")
    public Entity newE(SpawnData data) {
        return Entities.builder()
                .build();
    }

    @Spawns("type1")
    public Entity newE1(SpawnData data) {
        return Entities.builder()
                .build();
    }

    @Spawns("type2")
    public Entity newE2(SpawnData data) {
        return Entities.builder()
                .build();
    }
}
