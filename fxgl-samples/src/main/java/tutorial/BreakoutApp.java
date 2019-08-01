/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package tutorial;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.input.UserAction;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BreakoutApp extends GameApplication {

    private static final int PADDLE_WIDTH = 30;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 25;
    private static final int BALL_SIZE = 20;

    private static final int PADDLE_SPEED = 5;
    private static final int BALL_SPEED = 5;

    private Entity paddle1;
    private Entity paddle2;
    private Entity ball;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Breakout");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Up 1") {
            @Override
            protected void onAction() {
                paddle1.translateY(-PADDLE_SPEED);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Down 1") {
            @Override
            protected void onAction() {
                paddle1.translateY(PADDLE_SPEED);
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Up 2") {
            @Override
            protected void onAction() {
                paddle2.translateY(-PADDLE_SPEED);
            }
        }, KeyCode.UP);

        getInput().addAction(new UserAction("Down 2") {
            @Override
            protected void onAction() {
                paddle2.translateY(PADDLE_SPEED);
            }
        }, KeyCode.DOWN);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BreakoutFactory());

        paddle1 = spawn("bat", 0, getAppHeight() / 2 - PADDLE_HEIGHT / 2);
        paddle2 = spawn("bat", getAppWidth() - PADDLE_WIDTH, getAppHeight() / 2 - PADDLE_HEIGHT / 2);

        ball = spawn("ball", getAppWidth() / 2 - BALL_SIZE / 2, getAppHeight() / 2 - BALL_SIZE / 2);

        for (int i = 0; i < 10; i++) {
            spawn("brick", getAppWidth() / 2 - 200 - BRICK_WIDTH, 100 + i*(BRICK_HEIGHT + 20));
            spawn("brick", getAppWidth() / 2 + 200, 100 + i*(BRICK_HEIGHT + 20));
        }
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(BreakoutType.BALL, BreakoutType.BRICK, (ball, brick) -> {
            brick.removeFromWorld();
            Point2D velocity = ball.getObject("velocity");

            if (FXGLMath.randomBoolean()) {
                ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
            } else {
                ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
            }
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        Point2D velocity = ball.getObject("velocity");
        ball.translate(velocity);

        if (ball.getX() == paddle1.getRightX()
                && ball.getY() < paddle1.getBottomY()
                && ball.getBottomY() > paddle1.getY()) {
            ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
        }

        if (ball.getRightX() == paddle2.getX()
                && ball.getY() < paddle2.getBottomY()
                && ball.getBottomY() > paddle2.getY()) {
            ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
        }

        if (ball.getX() <= 0) {
            resetBall();
        }

        if (ball.getRightX() >= getAppWidth()) {
            resetBall();
        }

        if (ball.getY() <= 0) {
            ball.setY(0);
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }

        if (ball.getBottomY() >= getAppHeight()) {
            ball.setY(getAppHeight() - BALL_SIZE);
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }
    }

    private void resetBall() {
        ball.setPosition(getAppWidth() / 2 - BALL_SIZE / 2, getAppHeight() / 2 - BALL_SIZE / 2);
        ball.setProperty("velocity", new Point2D(BALL_SPEED, BALL_SPEED));
    }

    private enum BreakoutType {
        BRICK, BALL
    }

    public static class BreakoutFactory implements EntityFactory {

        @Spawns("bat")
        public Entity newBat(SpawnData data) {
            return entityBuilder()
                    .from(data)
                    .viewWithBBox(new Rectangle(PADDLE_WIDTH, PADDLE_HEIGHT))
                    .build();
        }

        @Spawns("ball")
        public Entity newBall(SpawnData data) {
            return entityBuilder()
                    .from(data)
                    .type(BreakoutType.BALL)
                    .viewWithBBox(new Rectangle(BALL_SIZE, BALL_SIZE, Color.BLUE))
                    .collidable()
                    .with("velocity", new Point2D(BALL_SPEED, BALL_SPEED))
                    .build();
        }

        @Spawns("brick")
        public Entity newBrick(SpawnData data) {
            return entityBuilder()
                    .from(data)
                    .type(BreakoutType.BRICK)
                    .viewWithBBox(new Rectangle(BRICK_WIDTH, BRICK_HEIGHT, Color.RED))
                    .collidable()
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
