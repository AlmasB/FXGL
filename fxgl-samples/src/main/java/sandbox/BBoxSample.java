/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.dsl.effects.SlowTimeEffect;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import dev.DeveloperWASDControl;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BBoxSample extends GameApplication {

    private enum Type {
        PLAYER, NPC
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("BBoxSample");
    }

    private Entity player;

    private double scaleX = 1.0;
    private double scaleY = 1.0;

    private double angle = 0;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Slow Time") {
            @Override
            protected void onActionBegin() {
                player.getComponent(EffectComponent.class).startEffect(new SlowTimeEffect(0.2, Duration.seconds(3)));
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Change view 1") {
            @Override
            protected void onActionBegin() {
                //byType(Type.NPC).get(0).getComponent(EffectComponent.class).startEffect(new FlashEffect(Color.RED, Duration.seconds(0.75)));

                //player.setView(texture("bird.png").toAnimatedTexture(2, Duration.seconds(0.33)).play());
            }
        }, KeyCode.G);

        onKey(KeyCode.Q, () -> {
            scaleX += 0.1;

            player.setScaleX(scaleX);
        });

        onKey(KeyCode.E, () -> {
            scaleY += 0.1;

            player.setScaleY(scaleY);
        });

        onKey(KeyCode.Z, () -> {
            scaleX -= 0.1;

            player.setScaleX(scaleX);
        });

        onKey(KeyCode.C, () -> {
            scaleY -= 0.1;

            player.setScaleY(scaleY);
        });

        onKey(KeyCode.R, () -> {
            angle -= 5;

            player.setRotation(angle);
        });

        onKey(KeyCode.T, () -> {
            angle += 5;

            player.setRotation(angle);
        });
    }

    @Override
    protected void initGame() {

        // entity 1

        player = entityBuilder()
                .type(Type.PLAYER)
                .at(100, 150)
                .viewWithBBox("brick.png")
                .with(new CollidableComponent(true), new DeveloperWASDControl())
                .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), 500))
                //.with(new LiftComponent().xAxisSpeedDuration(400, Duration.seconds(1)))
                //.with(new IntervalPauseComponent(Map.of(LiftComponent.class, Duration.seconds(2))))
                .with(new EffectComponent())
                .with(new TimeComponent(1.0))
                .zIndex(250)
                .buildAndAttach();

        player.getTransformComponent().setRotationOrigin(new Point2D(64, 64));

        player.getTransformComponent().scaleOriginXProperty().setValue(64);
        player.getTransformComponent().scaleOriginYProperty().setValue(64);

        // entity 2

        entityBuilder()
                .at(100, 100)
                .viewWithBBox(new Rectangle(40, 40, Color.RED))
                .with(new CollidableComponent(true))
                .with(new LiftComponent().yAxisSpeedDuration(150, Duration.seconds(3)).xAxisSpeedDuration(100, Duration.seconds(3)))
                .with(new EffectComponent())
                .zIndex(250)
                .buildAndAttach();

        // entity 3

        entityBuilder()
                .type(Type.NPC)
                .at(400, 150)
                .bbox(new HitBox(new Point2D(5, 5), BoundingShape.circle(20)))
                .view(texture("enemy1.png").toAnimatedTexture(2, Duration.seconds(1)).loop())
                .with(new EffectComponent())
                .buildAndAttach();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.NPC) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                t.setText("Collision");
            }

            @Override
            protected void onCollisionEnd(Entity a, Entity b) {
                t.setText("No collision");
            }
        });
    }

    private Text t;

    @Override
    protected void initUI() {
        t = new Text();
        t.setTranslateY(100);

        getGameScene().addUINode(t);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
