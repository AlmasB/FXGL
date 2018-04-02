/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots.anim;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotApp extends GameApplication {

    private RobotComponent robot;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1000);
        settings.setHeight(800);
        settings.setIntroEnabled(true);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                robot.left();
            }

            @Override
            protected void onActionEnd() {
                robot.stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                robot.right();
            }

            @Override
            protected void onActionEnd() {
                robot.stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Run Left") {
            @Override
            protected void onAction() {
                robot.runLeft();
            }

            @Override
            protected void onActionEnd() {
                robot.stop();
            }
        }, KeyCode.Q);

        getInput().addAction(new UserAction("Run Right") {
            @Override
            protected void onAction() {
                robot.runRight();
            }

            @Override
            protected void onActionEnd() {
                robot.stop();
            }
        }, KeyCode.E);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                robot.jump();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Crouch") {
            @Override
            protected void onActionBegin() {
                robot.crouch();
            }
        }, KeyCode.S);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new RobotFactory());

        Entity r = spawn("robot");

        Entities.animationBuilder()
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .delay(Duration.seconds(0.25))
                .duration(Duration.seconds(1))
                .scale(r)
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .buildAndPlay();

        robot = r.getComponent(RobotComponent.class);

        spawn("platform", new SpawnData(0, getHeight()).put("width", 3000).put("height", 40));

        spawn("platform", new SpawnData(300, getHeight() - 100).put("width", 120).put("height", 40));

        spawn("platform", new SpawnData(450, getHeight() - 250).put("width", 520).put("height", 40));
        spawn("platform", new SpawnData(950, getHeight() - 100).put("width", 120).put("height", 40));

        getGameScene().getViewport().bindToEntity(r, getWidth() / 2, 300);
        getGameScene().getViewport().setBounds(0, 0, 10000, getHeight() + 40);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 1400);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
