/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import dev.DeveloperWASDControl;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to init a basic game object and attach it to the world
 * using fluent API.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InitSample extends GameApplication {

    // 1. define types of entities in the game using Enum
    private enum Type {
        PLAYER, NPC
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InitSample");
        settings.setVersion("0.1");
    }

    private Entity player;

    private double scale = 1.0;
    private double scaleY = 1.0;

    private double angle = 0;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Change view") {
            @Override
            protected void onActionBegin() {
                //player.setView(new EntityView(new Rectangle(40, 30, Color.BLUE)));
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Change view 1") {
            @Override
            protected void onActionBegin() {
                //player.setView(texture("bird.png").toAnimatedTexture(2, Duration.seconds(0.33)).play());
            }
        }, KeyCode.G);

        onKey(KeyCode.Q, () -> {
            scale += 0.1;

            player.setScaleX(scale);
        });

        onKey(KeyCode.E, () -> {
            scaleY += 0.1;

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
        player = entityBuilder()
                .type(Type.PLAYER)
                .at(100, 150)
                .viewWithBBox("brick.png")
                .with(new CollidableComponent(true), new DeveloperWASDControl())
                .zIndex(250)
                .buildAndAttach();

        player.getTransformComponent().setRotationOrigin(new Point2D(64, 64));

        player.getTransformComponent().scaleOriginXProperty().setValue(64);
        player.getTransformComponent().scaleOriginYProperty().setValue(64);

        Entity player2 = new Entity();
        player2.setType(Type.NPC);
        player2.setPosition(100, 100);
        //player2.setView(new EntityView(new Rectangle(40, 40, Color.RED)));
        player2.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(40, 40)));
        player2.addComponent(new CollidableComponent(true));
        player2.getTransformComponent().setZ(250);

        getGameWorld().addEntity(player2);

        animationBuilder().delay(Duration.seconds(0.25))
                .duration(Duration.seconds(1))
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                //.repeat(2)
                .rotate(player2, player)
                .from(0)
                .to(360)
                .buildAndPlay();

        //translate(player, new Point2D(560, 300), Duration.seconds(2));

//        player.getViewComponent().addClickListener(() -> {
//            System.out.println("CLICKED");
//        });

//        entityBuilder()
//                .type(Type.PLAYER)
//                .at(120, 120)
//                .viewWithBBox(new Rectangle(40, 40, Color.GREEN))
//                .with(new CollidableComponent(true))
//                .zIndex(100)
//                .buildAndAttach();
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
    private GraphicsContext g;

    @Override
    protected void initUI() {
        t = new Text();
        t.setTranslateY(100);

        getGameScene().addUINode(t);

        Canvas canvas = new Canvas(getAppWidth(), getAppHeight());
        g = canvas.getGraphicsContext2D();
        g.setFill(Color.YELLOWGREEN);

        //getGameScene().addUINode(canvas);
    }

    @Override
    protected void onUpdate(double tpf) {
        g.clearRect(0, 0, getAppWidth(), getAppHeight());

//        var points = Debug.INSTANCE.getPoints();
//
//        while (!points.isEmpty()) {
//            var p = points.poll();
//
//            g.fillOval(p.getX() - 2.5, p.getY() - 2.5, 5, 5);
//        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
