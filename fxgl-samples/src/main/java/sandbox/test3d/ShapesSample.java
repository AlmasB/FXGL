/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.scene3d.*;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ShapesSample extends GameApplication {

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

        onKeyDown(KeyCode.F, () -> {
        });
    }

    private double lastX;
    private double lastY;

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();
        transform = getGameScene().getCamera3D().getTransform();

        camera3D.getTransform().translateZ(-10);
        camera3D.getTransform().translateY(0);
        //camera3D.getTransform().lookDownBy(45);

        getGameScene().setBackgroundColor(Color.LIGHTBLUE);

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

            if (Math.abs(offsetX) > 100 || Math.abs(offsetY) > 100)
                return;

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

        Node[] shapes = makeShapes();

        int x = 0;

        for (var shape : shapes) {
            var e = makeEntity(x * 2 - 8, 0, 6);

            e.getViewComponent().addChild(shape);

            x++;
        }

        // reset
        shapes = makeShapes();
        x = 0;

        for (var shape : shapes) {
            var e = makeEntity(x * 2 - 8, -4, 6);

            if (shape instanceof Shape3D) {
                ((Shape3D) shape).setMaterial(new PhongMaterial(Color.BLUE));
            }

            e.getViewComponent().addChild(shape);

            x++;
        }




        // TODO: add origin to gamescene

        var origin = makeEntity(0, 0, 0);
        var sp = new Sphere(0.1);
        sp.setMaterial(new PhongMaterial(Color.YELLOW));


        var axisX = new Box(1, 0.1, 0.1);
        axisX.setTranslateX(0.5);
        axisX.setMaterial(new PhongMaterial(Color.RED));

        var axisY = new Box(0.1, 1, 0.1);
        axisY.setTranslateY(0.5);
        axisY.setMaterial(new PhongMaterial(Color.GREEN));

        var axisZ = new Box(0.1, 0.1, 1);
        axisZ.setTranslateZ(0.5);
        axisZ.setMaterial(new PhongMaterial(Color.BLUE));

        origin.getViewComponent().addChild(sp);
        origin.getViewComponent().addChild(axisX);
        origin.getViewComponent().addChild(axisY);
        origin.getViewComponent().addChild(axisZ);

        // light

        newLight(2, -2, -4);
        newLight(-10, -4, 0);
    }

    private Node[] makeShapes() {
        return new Node[] {
                new Box(),
                new Sphere(),

                new Torus(),

                new Prism(1, 0.5, 2, 3),
                new Prism(),
                new Prism(1, 1, 2, 4),
                new Prism(1, 1, 2, 5),
                new Prism(1, 1, 2, 6),
                new Cylinder(),

                new Pyramid(),
                new Pyramid(1, 0, 2, 4),
                new Pyramid(1, 0, 2, 5),
                new Pyramid(1, 0, 2, 6),
                new Cone()
        };
    }

    private void newLight(double x, double y, double z) {
        var light = FXGL.entityBuilder()
                .at(x, y, z)
                .view(new Sphere(0.1))
                .view(new PointLight())
                .buildAndAttach();
    }

    private Entity makeEntity(double x, double y, double z) {
        var e = FXGL.entityBuilder()
                .at(x, y, z)
                .buildAndAttach();



        return e;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
