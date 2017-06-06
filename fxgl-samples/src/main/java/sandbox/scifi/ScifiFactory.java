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
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
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
                .with(new PhysicsComponent())
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
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .at(data.getX(), data.getY())
                .type(ScifiType.PLAYER)
                .bbox(new HitBox("main", BoundingShape.circle(19)))
                .with(physics)
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
