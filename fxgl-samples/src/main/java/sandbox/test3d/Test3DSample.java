/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.entity.components.TransformComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Test3DSample extends GameApplication {

    private TransformComponent transform;

    private Camera3D camera3D;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
        settings.setDeveloperMenuEnabled(true);
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

        onKeyDown(KeyCode.F, () -> {
            new Test3D().start();
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();
        transform = getGameScene().getCamera3D().getTransform();

        transform.translateZ(-10);

        getGameScene().setBackgroundColor(Color.LIGHTBLUE.darker().darker());

        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        new Test3D().start();
    }

    @Override
    protected void onUpdate(double tpf) {
        inc("score", +1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
