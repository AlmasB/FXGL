/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d.firingrange;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.TransformComponent;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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
        settings.setIntroEnabled(false);
    }

    private TransformComponent transform;

    private double lastX;
    private double lastY;

    private double cameraMoveSpeed = 0.5;

    private Entity c1, c2, c3;

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> {
            transform.moveForward(cameraMoveSpeed);
        });
        onKey(KeyCode.S, () -> {
            transform.moveBack(cameraMoveSpeed);
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

            var b = spawn("bullet", new SpawnData(0, 0).put("dir", transform.getDirection3D()));
            b.setPosition3D(transform.getPosition3D());
        });

        onKeyDown(KeyCode.DIGIT1, () -> {
            ObjectProperty<Color> color = c1.getObject("colorProperty");

            animationBuilder()
                    .animate(color)
                    .from(color.getValue())
                    .to(FXGLMath.randomColor())
                    .buildAndPlay();
        });
        onKeyDown(KeyCode.DIGIT2, () -> {
            ObjectProperty<Color> color = c2.getObject("colorProperty");

            animationBuilder()
                    .animate(color)
                    .from(color.getValue())
                    .to(FXGLMath.randomColor())
                    .buildAndPlay();
        });
        onKeyDown(KeyCode.DIGIT3, () -> {
            ObjectProperty<Color> color = c3.getObject("colorProperty");

            animationBuilder()
                    .animate(color)
                    .from(color.getValue())
                    .to(FXGLMath.randomColor())
                    .buildAndPlay();
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKCYAN.brighter().brighter());

        // 3D specific stuff, some of it may be merged with engine
        transform = getGameScene().getCamera3D().getTransform();

        getGameScene().setMouseGrabbed(true);
        getGameScene().setCursorInvisible();

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

        transform.translateY(-10);

        // normal stuff

        getGameWorld().addEntityFactory(new FiringRangeFactory());

        spawn("levelBox");
        spawn("light", 0, -10);

        for (int i = 0; i < 5; i++) {
            var e = spawn("target", new SpawnData(-10 + i * 6, -15).put("delay", i * 0.25));
            e.setZ(15);
        }

        c1 = spawn("coin", -5, -7, 4);
        c2 = spawn("coin", 0, -7, 4);
        c3 = spawn("coin", +5, -7, 4);

        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .repeatInfinitely()
                .rotate(c1)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(360, 0, 0))
                .buildAndPlay();

        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.SMOOTH.EASE_IN())
                .repeatInfinitely()
                .rotate(c2)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(0, 360, 0))
                .buildAndPlay();

        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .repeatInfinitely()
                .rotate(c3)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(0, 0, 360))
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
