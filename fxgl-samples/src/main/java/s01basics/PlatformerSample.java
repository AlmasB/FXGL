/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Sample that shows how to use ChainShape for platforms.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PlatformerSample extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
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
                    physics.setVelocityY(-300);
                }
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
    }

    @Override
    protected void initGame() {
        createPlatforms();
        player = createPlayer(100, 100, 40, 60);

        player.getTransformComponent().setScaleOrigin(new Point2D(20, 30));
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
    }

    private Entity createPlayer(double x, double y, double width, double height) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.addGroundSensor(new HitBox(new Point2D(0, height - 5), BoundingShape.box(width, 10)));
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(width, height, Color.BLUE))
                .with(physics)
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}