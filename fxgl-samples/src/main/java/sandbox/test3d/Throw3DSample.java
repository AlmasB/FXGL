/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.scene3d.Cuboid;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import sandbox.test3d.firingrange.Projectile3DComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Throw3DSample extends GameApplication {

    private enum Type {
        GROUND, GRENADE
    }

    private Camera3D camera3D;

    private Entity model;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initInput() {
        onBtnDownPrimary(() -> {
            shoot(camera3D.getTransform().getDirection3D());
        });

        onKeyDown(KeyCode.L, () -> {
            getGameController().exit();
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTCYAN);

        getGameScene().setCursorInvisible();
        getGameScene().setFPSCamera(true);

        camera3D = getGameScene().getCamera3D();
        camera3D.getTransform().translateY(-5);
        camera3D.getTransform().lookAt(Point3D.ZERO);

        spawnPlane(0, 3, 10);
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(Type.GRENADE, Type.GROUND, (grenade, ground) -> {
            spawnExplosion(grenade.getPosition3D());
            grenade.removeFromWorld();
        });
    }

    private Entity spawnPlane(double x, double y, double z) {
        return entityBuilder()
                .type(Type.GROUND)
                .at(x, y, z)
                .bbox(new HitBox(BoundingShape.box3D(10, 0.2, 10)))
                .view(new Cuboid(10, 0.2, 10))
                .collidable()
                .buildAndAttach();
    }

    private void shoot(Point3D direction) {
        var model = getAssetLoader().loadModel3D("grenade/Grenade.obj");
        model.setScaleX(2);
        model.setScaleY(-2);
        model.setScaleZ(2);

        var e = entityBuilder()
                .type(Type.GRENADE)
                .at(camera3D.getTransform().getPosition3D())
                .bbox(new HitBox(BoundingShape.box3D(1, 1, 1)))
                .view(model)
                .with(new Projectile3DComponent(direction.subtract(0, 0.5, 0), 25))
                .with(new Component() {
                    private double accel = 0.05;

                    @Override
                    public void onUpdate(double tpf) {
                        accel += 0.4;
                        entity.translateY(tpf * accel);

                        if (entity.getY() > 20) {
                            entity.removeFromWorld();
                        }
                    }
                })
                .collidable()
                .buildAndAttach();
    }

    private Entity spawnExplosion(Point3D pos) {
        return entityBuilder()
                .at(pos.add(-60, 0, 150))
                .view(texture("explosion2.png").toAnimatedTexture(16, Duration.seconds(1)).play())
                .with(new ExpireCleanComponent(Duration.seconds(1.0)))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
