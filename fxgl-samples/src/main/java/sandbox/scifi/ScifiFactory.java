/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.scifi;

import com.almasb.fxgl.entity.*;
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
public class ScifiFactory implements EntityFactory {

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(ScifiType.PLATFORM)
                .bbox(new HitBox("main", BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent(), new CollidableComponent(true))
                .build();
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(ScifiType.COIN)
                .viewFromTextureWithBBox("coin.png")
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().friction(0).density(0.25f));

        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true);

        physics.setBodyDef(bd);
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .from(data)
                .type(ScifiType.PLAYER)
                .bbox(new HitBox("main", BoundingShape.circle(15)))
                .bbox(new HitBox("lower", new Point2D(15 - 5, 30), BoundingShape.box(10, 10)))
                .with(physics, new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    @Spawns("portal")
    public Entity newPortal(SpawnData data) {
        return Entities.builder()
                .type(ScifiType.PORTAL)
                .from(data)
                .viewFromTextureWithBBox("finish.png")
                .with(new CollidableComponent(true))
                .build();
    }
}
