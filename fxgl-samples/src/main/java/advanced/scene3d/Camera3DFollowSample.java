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
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.scene3d.Cuboid;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.input.KeyCode.*;

/**
 * Shows how to use a 3D camera and make it follow a given entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Camera3DFollowSample extends GameApplication {

    private Entity e;
    private Camera3D camera3D;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initInput() {
        double speed = 0.15;

        onKey(W, () -> e.getTransformComponent().moveForwardXZ(speed));
        onKey(S, () -> e.getTransformComponent().moveBackXZ(speed));
        onKey(A, () -> e.getTransformComponent().moveLeft(speed));
        onKey(D, () -> e.getTransformComponent().moveRight(speed));
        onKey(UP, () -> e.getTransformComponent().translateY(-speed));
        onKey(DOWN, () -> e.getTransformComponent().translateY(speed));

        onKeyDown(L, () -> getGameController().exit());
    }

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();

        getGameScene().setBackgroundColor(Color.DARKCYAN);

        var cuboid = new Cuboid(2, 2, 2);
        cuboid.setPhongMaterial(Color.BLUE);

        e = entityBuilder()
                .view(cuboid)
                .collidable()
                .buildAndAttach();

        camera3D.getTransform().bindToLookAt3D(e.getTransformComponent());

        // populate boxes

        for (int z = -1; z <= 1; z++) {
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    var cube = new Cuboid(3 * 0.25, 3 * 0.25, 3);
                    cube.setPhongMaterial(FXGLMath.randomColor());

                    var scale = 15.0;

                    var e2 = entityBuilder()
                            .at(x * scale, y * scale, z * scale)
                            .view(cube)
                            .buildAndAttach();

                    e2.getTransformComponent().bindToLookAt3D(e.getTransformComponent());
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
