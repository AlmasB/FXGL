/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.TransformComponent;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ThirdPersonCamera3DSample extends GameApplication {
    private TransformComponent transform;

    private Camera3D camera3D;

    private Entity agent;
    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> {
            player.getTransformComponent().moveForward(0.5);
        });
        onKey(KeyCode.S, () -> {
            player.getTransformComponent().moveBack(0.5);
        });
        onKey(KeyCode.A, () -> {
            player.getTransformComponent().moveLeft(0.5);
        });
        onKey(KeyCode.D, () -> {
            player.getTransformComponent().moveRight(0.5);
        });

        onKey(KeyCode.UP, () -> {
            player.getTransformComponent().lookUpBy(2);
        });
        onKey(KeyCode.DOWN, () -> {
            player.getTransformComponent().lookDownBy(2);
        });
        onKey(KeyCode.LEFT, () -> {
            player.getTransformComponent().lookLeftBy(2);
        });
        onKey(KeyCode.RIGHT, () -> {
            player.getTransformComponent().lookRightBy(2);
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

        getGameScene().setBackgroundColor(Color.LIGHTBLUE);

        //getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        for (int i = 0; i < 10; i++) {
            makeAgent(new Point3D(i * 3, 0, i * 3));
        }

        var body = new Box();
        body.setMaterial(new PhongMaterial(Color.BLUE));

        player = entityBuilder()
                .view(body)
                .buildAndAttach();
    }

    @Override
    protected void onUpdate(double tpf) {
        var pos = player.getPosition3D().subtract(player.getTransformComponent().getDirection3D().multiply(8));

        transform.setPosition3D(pos);
        transform.lookAt(player.getPosition3D());
    }

    private void makeAgent(Point3D p) {
        var body = new Box();
        body.setMaterial(new PhongMaterial(FXGLMath.randomColor()));

        var front = new Sphere(0.2);
        front.setMaterial(new PhongMaterial(Color.RED));
        front.setTranslateZ(1);

        agent = entityBuilder()
                .at(p)
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
