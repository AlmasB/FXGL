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
import com.almasb.fxgl.scene3d.Cuboid;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;
import static java.lang.Math.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MinecraftSample extends GameApplication {

    private Camera3D camera3D;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> camera3D.moveForward());
        onKey(KeyCode.S, () -> camera3D.moveBack());
        onKey(KeyCode.A, () -> camera3D.moveLeft());
        onKey(KeyCode.D, () -> camera3D.moveRight());
        onKey(KeyCode.L, () -> getGameController().exit());

        onBtnDown(MouseButton.PRIMARY, () -> {
            var pos = camera3D.getTransform().getPosition3D().add(camera3D.getTransform().getDirection3D().multiply(1.5));

            spawnCube(new Point3D(round(pos.getX()), round(pos.getY()), round(pos.getZ())));
        });
    }

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();

        getGameScene().setBackgroundColor(Color.LIGHTBLUE.darker());
        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();
    }

    private Entity spawnCube(Point3D pos) {
        return entityBuilder()
                .at(pos)
                .view(new Cuboid(1, 1, 1))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
