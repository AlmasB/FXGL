/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.FollowComponent;
import com.almasb.fxgl.dsl.views.MinimapView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Sample that shows how to use ChainShape for platforms.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PlatformerSample extends GameApplication {

    private Entity player;
    private Entity poly;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1200);
        settings.setHeight(600);
        settings.setTitle("PlatformerSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Left") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PhysicsComponent.class).setVelocityX(-200);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Right") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PhysicsComponent.class).setVelocityX(200);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                PhysicsComponent physics = player.getComponent(PhysicsComponent.class);

                if (physics.isOnGround()) {
                    physics.setVelocityY(-500);
                }

                getDevPane().addDebugPoint(player.getCenter());
            }
        }, KeyCode.W);

        onKeyDown(KeyCode.I, "Info", () -> System.out.println(player.getCenter()));

        input.addAction(new UserAction("Grow") {
            @Override
            protected void onActionBegin() {


                player.setScaleX(player.getScaleX() * 1.25);
                player.setScaleY(player.getScaleY() * 1.25);

//                double x = player.getX();
//                double y = player.getY();
//
//                player.removeFromWorld();
//
//                player = createPlayer(x, y, 60, 80);
            }
        }, KeyCode.SPACE);

        input.addAction(new UserAction("Grow Poly") {
            @Override
            protected void onActionBegin() {
                poly.setScaleX(poly.getScaleX() * 1.25);
                poly.setScaleY(poly.getScaleY() * 1.25);
            }
        }, KeyCode.G);
    }

    @Override
    protected void initGame() {
        entityBuilder().buildScreenBoundsAndAttach(40);

        createPlatforms();
        player = createPlayer(100, 100, 40, 60);

        player.getTransformComponent().setScaleOrigin(new Point2D(20, 30));
        player.getComponent(PhysicsComponent.class).onGroundProperty().addListener((o, oldValue, newValue) -> {
            System.out.println(newValue ? "On Ground" : "In the air");
        });

        entityBuilder()
                .at(300, 150)
                .view(new Circle(25, Color.RED))
                .with(new FollowComponent(player, 150, 50, 60).setMoveDelay(Duration.seconds(2)))
                .buildAndAttach();
    }

    private void createPlatforms() {
        entityBuilder()
                .at(0, 500)
                .view(new Rectangle(120, 100, Color.GRAY))
                .bbox(new HitBox("Main", BoundingShape.chain(
                        new Point2D(0, 0),
                        new Point2D(120, 0),
                        new Point2D(120, 100),
                        new Point2D(0, 100)
                )))
                .with(new PhysicsComponent())
                .buildAndAttach();

        entityBuilder()
                .at(180, 500)
                .view(new Rectangle(400, 100, Color.GRAY))
                .bbox(new HitBox("Main", BoundingShape.chain(
                        new Point2D(0, 0),
                        new Point2D(400, 0),
                        new Point2D(400, 100),
                        new Point2D(0, 100)
                )))
                .with(new PhysicsComponent())
                .buildAndAttach();

        var view = new Group();

        var first = new Polygon(
                0, 0, 200, 0, 250, 100, 0, 30
        );
        first.setTranslateX(-15);
        first.setTranslateY(10);

        view.getChildren().add(first);

        Polygon second = new Polygon(
                0, -30, 30, -30, 60, 30, 0, 30
        );
        second.setTranslateX(280);
        second.setTranslateY(50);
        view.getChildren().add(second);

        var third = new Rectangle(30, 30);
        third.setTranslateX(250);
        third.setTranslateY(-30);
        view.getChildren().add(third);

        poly = entityBuilder()
                .at(180, 350)
                .view(view)
                .bbox(new HitBox("Main", new Point2D(-15, 10), BoundingShape.polygon(
                        new Point2D(0, 0),
                        new Point2D(200, 0),
                        new Point2D(250, 100),
                        new Point2D(0, 30)
                )))
                .bbox(new HitBox("2nd", new Point2D(250, -30), BoundingShape.box(30, 30)))
                //.bbox(new HitBox("4th", new Point2D(280, 0), BoundingShape.box(30, 30)))
                .bbox(new HitBox("3rd", new Point2D(280, 50), BoundingShape.polygon(
                        new Point2D(0, -30),
                        new Point2D(30, -30),
                        new Point2D(60, 30),
                        new Point2D(0, 30)
                )))
                .with(new PhysicsComponent())
                .buildAndAttach();
    }

    private Entity createPlayer(double x, double y, double width, double height) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.addGroundSensor(new HitBox(new Point2D(5, height - 5), BoundingShape.box(width - 10, 10)));
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(width, height, Color.BLUE))
                .with(physics)
                .buildAndAttach();
    }

    @Override
    protected void initUI() {
        var minimap = new MinimapView(getGameWorld(), 800, 600, 200, 100);
        minimap.setEntityColor(Color.GREEN);

        addUINode(minimap, getAppWidth() - 210, getAppHeight() - 110);
    }

    public static void main(String[] args) {
        launch(args);
    }
}