/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s04physics;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Left") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PhysicsComponent.class).setLinearVelocity(new Point2D(-200, 0));
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Right") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PhysicsComponent.class).setLinearVelocity(new Point2D(200, 0));
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                PhysicsComponent physics = player.getComponent(PhysicsComponent.class);

                if (physics.isOnGround()) {
                    double dx = physics.getLinearVelocity().getX();

                    physics.setLinearVelocity(new Point2D(dx, -100));
                }
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Grow") {
            @Override
            protected void onActionBegin() {
                double x = player.getX();
                double y = player.getY();

                player.removeFromWorld();

                player = createPlayer(x, y, 60, 80);
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initGame() {
        createPlatforms();
        player = createPlayer(100, 100, 40, 60);
    }

    private void createPlatforms() {
        Entities.builder()
                .at(0, 500)
                .viewFromNode(new Rectangle(120, 100, Color.GRAY))
                .bbox(new HitBox("Main", BoundingShape.chain(
                        new Point2D(0, 0),
                        new Point2D(120, 0),
                        new Point2D(120, 100),
                        new Point2D(0, 100)
                )))
                .with(new PhysicsComponent())
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(180, 500)
                .viewFromNode(new Rectangle(400, 100, Color.GRAY))
                .bbox(new HitBox("Main", BoundingShape.chain(
                        new Point2D(0, 0),
                        new Point2D(400, 0),
                        new Point2D(400, 100),
                        new Point2D(0, 100)
                )))
                .with(new PhysicsComponent())
                .buildAndAttach(getGameWorld());
    }

    private Entity createPlayer(double x, double y, double width, double height) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setGenerateGroundSensor(true);
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .at(x, y)
                .viewFromNodeWithBBox(new Rectangle(width, height, Color.BLUE))
                .with(physics)
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
