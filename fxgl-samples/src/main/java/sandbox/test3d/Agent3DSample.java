/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.TransformComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Agent3DSample extends GameApplication {
    private TransformComponent transform;

    private Camera3D camera3D;

    private Entity agent;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> {
            camera3D.moveForward();
        });
        onKey(KeyCode.S, () -> {
            camera3D.moveBack();
        });
        onKey(KeyCode.A, () -> {
            camera3D.moveLeft();
        });
        onKey(KeyCode.D, () -> {
            camera3D.moveRight();
        });

        onKey(KeyCode.UP, () -> {
            agent.getTransformComponent().lookUpBy(2);
        });
        onKey(KeyCode.DOWN, () -> {
            agent.getTransformComponent().lookDownBy(2);
        });
        onKey(KeyCode.LEFT, () -> {
            agent.getTransformComponent().lookLeftBy(2);
        });
        onKey(KeyCode.RIGHT, () -> {
            agent.getTransformComponent().lookRightBy(2);
        });

        onKey(KeyCode.L, () -> {
            getGameController().exit();
        });

        onKeyDown(KeyCode.F, () -> {
        });
    }

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();
        transform = getGameScene().getCamera3D().getTransform();

        transform.translateZ(-10);
        transform.translateY(0);

        getGameScene().setBackgroundColor(Color.LIGHTBLUE);

        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        makeAgent();
    }

    private void makeAgent() {
        var body = new Box();
        body.setMaterial(new PhongMaterial(Color.DARKBLUE));

        var front = new Sphere(0.2);
        front.setMaterial(new PhongMaterial(Color.RED));
        front.setTranslateZ(1);

        agent = entityBuilder()
                .with(new Component() {
                    @Override
                    public void onUpdate(double tpf) {
                        var dir = entity.getTransformComponent().getDirection3D();
                        entity.getTransformComponent().translate3D(dir.multiply(0.01));
                    }
                })
                .view(body)
                .view(front)
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
