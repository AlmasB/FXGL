/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.scene3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.scene3d.Cuboid;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use a 3D camera.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Camera3DSample extends GameApplication {

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

        // place the camera at origin (by default the camera is moved slightly back)
        camera3D.getTransform().setZ(0);

        getGameScene().setBackgroundColor(Color.DARKCYAN);

        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        // populate boxes

        for (int z = -1; z <= 1; z++) {
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    var cube = new Cuboid(1, 1, 1);
                    cube.setPhongMaterial(FXGLMath.randomColor());

                    var scale = 15.0;

                    entityBuilder()
                            .at(x * scale, y * scale, z * scale)
                            .view(cube)
                            .buildAndAttach();
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
