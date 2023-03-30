/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.scene3d.Cuboid;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.input.KeyCode.*;

/**
 * Sample for collisions in 3D space.
 * TODO: not implemented
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Collision3DSample extends GameApplication {

    private enum Type {
        BOX
    }

    private Entity e;
    private Text text;

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
        getGameScene().setBackgroundColor(Color.AQUA);
        getGameScene().setFPSCamera(true);

        var cuboid = new Cuboid(2, 2, 2);
        cuboid.setPhongMaterial(Color.BLUE);

        e = entityBuilder()
                .bbox(BoundingShape.box3D(2, 2, 2))
                .view(cuboid)
                .type(Type.BOX)
                .collidable()
                .buildAndAttach();

        var camera = getGameScene().getCamera3D();
        camera.getTransform().xProperty().bind(e.xProperty());
        camera.getTransform().yProperty().bind(e.yProperty().subtract(10));
        camera.getTransform().zProperty().bind(e.zProperty().subtract(10));

        // build scene with some cuboids

        var e1 = entityBuilder()
                .at(-8, 0, 0)
                .bbox(BoundingShape.box3D(2, 2, 2))
                .view(new Cuboid(2))
                .type(Type.BOX)
                .collidable()
                .buildAndAttach();

        var e2 = entityBuilder()
                .at(0, 0, 0)
                .bbox(BoundingShape.box3D(2, 2, 2))
                .view(new Cuboid(2))
                .rotate(35)
                .type(Type.BOX)
                .collidable()
                .buildAndAttach();

        var e3 = entityBuilder()
                .at(8, 0, 0)
                .bbox(BoundingShape.box3D(6, 2, 2))
                .view(new Cuboid(6, 2, 2))
                .type(Type.BOX)
                .collidable()
                .buildAndAttach();

        e3.getTransformComponent().setRotationX(45);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(Type.BOX, Type.BOX) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                text.setText("Collision");
            }

            @Override
            protected void onCollisionEnd(Entity a, Entity b) {
                text.setText("No collision");
            }
        });
    }

    @Override
    protected void initUI() {
        text = getUIFactoryService().newText("", Color.BLACK, 14.0);

        addUINode(text, 50, 50);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
