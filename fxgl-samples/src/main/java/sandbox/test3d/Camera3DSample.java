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
import com.almasb.fxgl.entity.components.TransformComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Camera3DSample extends GameApplication {

    private TransformComponent transform;

    private Camera3D camera3D;

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

        onKey(KeyCode.L, () -> {
            getGameController().exit();
        });
    }

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();
        transform = getGameScene().getCamera3D().getTransform();
        transform.setZ(0);

        getGameScene().setBackgroundColor(Color.DARKCYAN);

        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        // populate boxes

        for (int z = -1; z <= 1; z++) {
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    var box = new Box(1, 1, 1);
                    box.setMaterial(new PhongMaterial(FXGLMath.randomColor()));

                    var scale = 15.0;

                    entityBuilder()
                            .at(x * scale, y * scale, z * scale)
                            .view(box)
                            .buildAndAttach();
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
