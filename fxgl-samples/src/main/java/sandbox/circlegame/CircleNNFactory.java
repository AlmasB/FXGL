/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.circlegame;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
import static sandbox.circlegame.CircleNNType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CircleNNFactory implements EntityFactory {

    @Spawns("circle")
    public Entity newCircle(SpawnData data) {
        var color = FXGLMath.randomColor().brighter();
        var view = new Circle(32, 32, 30, null);
        view.setStrokeWidth(2);
        view.setStroke(color);

        return entityBuilder(data)
                .type(CIRCLE)
                .bbox(new HitBox(BoundingShape.box(64, 64)))
                .view(view)
                .collidable()
                .with(new HealthIntComponent(49))
                .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), 300))
                .with(new CircleComponent())
                .with(new BlockCollisionComponent())
                .build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        Point2D dir = data.get("dir");

        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox(new Rectangle(20, 2, Color.WHITE))
                .collidable()
                .with(new ProjectileComponent(dir, 800))
                .with(new OffscreenCleanComponent())
                .build();
    }

    @Spawns("block")
    public Entity newBlock(SpawnData data) {
        return entityBuilder(data)
                .type(BLOCK)
                .viewWithBBox("brick.png")
                .collidable()
                .build();
    }
}
