/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d.firingrange;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.TransformComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FiringRangeApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setExperimental3D(true);
    }

    private TransformComponent transform;

    private double lastX;
    private double lastY;

    private double cameraMoveSpeed = 0.3;

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> {
            transform.moveForwardXZ(cameraMoveSpeed);
        });
        onKey(KeyCode.S, () -> {
            transform.moveBackXZ(cameraMoveSpeed);
        });
        onKey(KeyCode.A, () -> {
            transform.moveLeft(cameraMoveSpeed);
        });
        onKey(KeyCode.D, () -> {
            transform.moveRight(cameraMoveSpeed);
        });

        onKey(KeyCode.L, () -> {
            getGameController().exit();
        });

        onBtnDown(MouseButton.PRIMARY, () -> {
            spawn("bullet", new SpawnData(transform.getPosition3D()).put("dir", transform.getDirection3D()));
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKCYAN.brighter().brighter());

        transform = getGameScene().getCamera3D().getTransform();
        transform.setY(-15);

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

        // normal stuff

        getGameWorld().addEntityFactory(new FiringRangeFactory());

        spawn("levelBox");
        spawn("light", 0, -10);

        run(() -> {
            spawn("target", -25, -15, 25);
        }, Duration.seconds(0.5));
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(FiringRangeEntityType.BULLET, FiringRangeEntityType.TARGET, (bullet, target) -> {
            inc("score", +1);

            bullet.removeFromWorld();

            Box box = (Box) target.getViewComponent().getChildren().get(0);
            PhongMaterial mat = (PhongMaterial) box.getMaterial();

            mat.setDiffuseColor(Color.RED);

            target.getComponent(CollidableComponent.class).setValue(false);
            target.setUpdateEnabled(false);

            target.translateZ(3);

            animationBuilder()
                    .onFinished(() -> target.removeFromWorld())
                    .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                    .translate(target)
                    .from(target.getPosition3D())
                    .to(target.getPosition3D().subtract(0, 10, 0))
                    .buildAndPlay();
        });
    }

    @Override
    protected void initUI() {
        var text = addVarText("score", getAppWidth() - 100, 50);
        text.setFill(Color.BLACK);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
