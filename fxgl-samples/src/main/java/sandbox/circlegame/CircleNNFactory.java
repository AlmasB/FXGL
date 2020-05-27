/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.circlegame;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static sandbox.circlegame.CircleNNType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CircleNNFactory implements EntityFactory {

    @Spawns("circle")
    public Entity newCircle(SpawnData data) {
        var circleComponent = new CircleComponent();

        var color = FXGLMath.randomColor().brighter();
        var view = new Circle(32, 32, 30, null);
        view.setStrokeWidth(2);
        view.setStroke(color);

        var hp = new HealthIntComponent(49);

        var viewHP = new Circle(32, 32, 30);
        viewHP.radiusProperty().bind(hp.valueProperty().divide(49.0).multiply(30));

        var viewRank = getUIFactoryService().newText("", Color.MIDNIGHTBLUE, 12.0);
        viewRank.setTranslateX(32);
        viewRank.setTranslateY(32);

        var e = entityBuilder(data)
                .type(CIRCLE)
                .bbox(new HitBox(BoundingShape.box(64, 64)))
                .view(view)
                .view(viewHP)
                //.view(viewRank)
                .collidable()
                .with("isShielded", false)
                .with("rank", 1)
                .with(new TimeComponent())
                .with(new EffectComponent())
                .with(hp)
                .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), 300))
                .with(circleComponent)
                .with(new BlockCollisionComponent())
                .build();

        viewHP.fillProperty().bind(
                Bindings.when(e.getProperties().booleanProperty("isShielded"))
                        .then(Color.WHITE)
                        .otherwise(Color.color(0.5, 0.78, 0.2, 0.5).brighter())
        );

        viewRank.textProperty().bind(e.getProperties().intProperty("rank").asString());

        return e;
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        Point2D dir = data.get("dir");

        var view = new Rectangle(30, 2, Color.WHITE);
        view.setArcWidth(15);
        view.setArcHeight(15);

        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox(view)
                .collidable()
                .with(new ProjectileComponent(dir, 800))
                .with(new OffscreenCleanComponent())
                .with("damage", data.hasKey("damage") ? data.get("damage") : 1)
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

    @Spawns("powerup")
    public Entity newPowerup(SpawnData data) {
        PowerupType type = data.get("powerupType");

        return entityBuilder(data)
                .type(POWERUP)
                .viewWithBBox(type.textureName)
                .collidable()
                .build();
    }

    @Spawns("explosion")
    public Entity spawnExplosion(SpawnData data) {
        var e = entityBuilder()
                .at(data.getX() - 40, data.getY() - 40)
                .view(texture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(0.75)).play())
                .with(new ExpireCleanComponent(Duration.seconds(1.6)))
                .build();
        return e;
    }
}
