/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d.snake;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.input.KeyCode.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SnakeApp extends GameApplication {

    enum SnakeType {
        SNAKE, FOOD
    }

    private SnakeComponent snakeComponent;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initInput() {
        onKeyDown(W, () -> snakeComponent.moveForward());
        onKeyDown(S, () -> snakeComponent.moveBack());
        onKeyDown(A, () -> snakeComponent.moveLeft());
        onKeyDown(D, () -> snakeComponent.moveRight());
        onKeyDown(UP, () -> snakeComponent.moveUp());
        onKeyDown(DOWN, () -> snakeComponent.moveDown());

        onKeyDown(F, () -> snakeComponent.grow());
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.CORNFLOWERBLUE);

        var camera = getGameScene().getCamera3D();
        camera.getTransform().translateZ(-5);
        camera.getTransform().translateY(-15);
        camera.getTransform().lookDownBy(45);

        getGameWorld().addEntityFactory(new SnakeFactory());

        var snake = spawn("player");
        snakeComponent = snake.getComponent(SnakeComponent.class);

        var food = spawn("food");

        teleportFood(food);
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(SnakeType.SNAKE, SnakeType.FOOD, (snake, food) -> {
            teleportFood(food);
            snakeComponent.grow();
        });
    }

    private void teleportFood(Entity food) {
        food.setOpacity(0);
        food.setScaleUniform(0);

        food.setX(random(-5, 5));
        food.setY(random(-5, 5));
        food.setZ(random(-5, 5));

        animationBuilder()
                .fadeIn(food)
                .buildAndPlay();

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .scale(food)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(1, 1, 1))
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
