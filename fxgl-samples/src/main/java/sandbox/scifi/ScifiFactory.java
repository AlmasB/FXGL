/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.scifi;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class ScifiFactory implements EntityFactory {

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return Entities.builder()
                .at(data.getX(), data.getY())
                .type(ScifiType.PLATFORM)
                .bbox(new HitBox("main", BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent(), new CollidableComponent(true))
                .build();
    }

    @Spawns("block")
    public Entity newBlock(SpawnData data) {
        return Entities.builder()
                .at(data.getX(), data.getY())
                .type(ScifiType.PLATFORM)
                .viewFromNodeWithBBox(new EntityView(new Rectangle(640 - 512, 64, Color.DARKCYAN), new RenderLayer() {
                    @Override
                    public String name() {
                        return "Block";
                    }

                    @Override
                    public int index() {
                        return 10000;
                    }
                }))
                .with(new PhysicsComponent())
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
                .at(data.getX(), data.getY())
                .type(ScifiType.PLAYER)
                .bbox(new HitBox("main", BoundingShape.circle(15)))
                .bbox(new HitBox("lower", new Point2D(15 - 5, 30), BoundingShape.box(10, 10)))
                .with(physics, new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    @Spawns("button")
    public Entity newButton(SpawnData data) {
        return Entities.builder()
                .at(data.getX(), data.getY())
                .type(ScifiType.BUTTON)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("push_button.png", 33, 22))
                .with(new UsableControl(() -> FXGL.getApp().getGameWorld().spawn("block", 256, 352)))
                .build();
    }
}
