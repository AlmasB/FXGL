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
import javafx.scene.input.MouseEvent;
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
        settings.setExperimental3D(true);
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

    private double lastX;
    private double lastY;

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();
        transform = getGameScene().getCamera3D().getTransform();
        transform.setZ(0);

        getGameScene().setBackgroundColor(Color.DARKCYAN);

        getGameScene().setMouseGrabbed(true);
        getGameScene().setCursorInvisible();

        // TODO: add max rotation clamp, e.g. start of game
        // merge common stuff with engine
        getInput().addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            var mouseX = getInput().getMouseXWorld();
            var mouseY = getInput().getMouseYWorld();

            if (e.getScreenX() == 960.0 && e.getScreenY() == 540.0) {
                // ignore warp mouse events
                lastX = mouseX;
                lastY = mouseY;
                return;
            }

            var offsetX = mouseX - lastX;
            var offsetY = mouseY - lastY;

            var mouseSensitivity = 0.2;

            if (Math.abs(offsetX) > 0.5) {
                if (mouseX > lastX) {
                    transform.lookRightBy(mouseSensitivity * (mouseX - lastX));
                } else if (mouseX < lastX) {
                    transform.lookLeftBy(mouseSensitivity * (lastX -  mouseX));
                }
            }

            if (Math.abs(offsetY) > 0.5) {
                if (mouseY > lastY) {
                    transform.lookDownBy(mouseSensitivity * (mouseY - lastY));
                } else if (mouseY < lastY) {
                    transform.lookUpBy(mouseSensitivity * (lastY - mouseY));
                }
            }

            lastX = mouseX;
            lastY = mouseY;
        });

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
