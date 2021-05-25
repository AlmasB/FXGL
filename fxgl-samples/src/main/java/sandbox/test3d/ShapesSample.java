/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.scene3d.*;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

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
        settings.set3D(true);
//        settings.setFullScreenAllowed(true);
//        settings.setFullScreenFromStart(true);
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

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();
        transform = getGameScene().getCamera3D().getTransform();

        transform.translateZ(-10);
        transform.translateY(0);

        getGameScene().setBackgroundColor(Color.LIGHTBLUE);

        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        boolean isAnimated = true;

        Node[] shapes = makeShapes();

        int x = 0;

        for (var shape : shapes) {
            var e = makeEntity(x * 2 - 8, 0, 6);

            e.getViewComponent().addChild(shape);

            if (isAnimated) {
                animationBuilder()
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .delay(Duration.seconds(x * 0.5))
                        .repeatInfinitely()
                        .autoReverse(true)
                        .translate(e)
                        .from(e.getPosition3D())
                        .to(e.getPosition3D().add(0, -2, 0))
                        .buildAndPlay();

                animationBuilder()
                        //.interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                        .duration(Duration.seconds(2.0))
                        .delay(Duration.seconds(x * 0.5))
                        .repeatInfinitely()
                        .autoReverse(true)
                        .rotate(e)
                        .from(new Point3D(0, 0, 0))
                        .to(new Point3D(360, 0, 0))
                        .buildAndPlay();
            }

            x++;
        }

        // reset
        shapes = makeShapes();
        x = 0;

        for (var shape : shapes) {
            var e = makeEntity(x * 2 - 8, -4, 6);

            if (shape instanceof Shape3D) {
                Shape3D s = ((Shape3D) shape);

                var mat = new PhongMaterial();
                mat.setDiffuseMap(image("brick.png"));

                s.setMaterial(mat);
            } else if (shape instanceof Model3D) {
                Model3D s = ((Model3D) shape);

                var mat = new PhongMaterial();
                mat.setDiffuseMap(image("brick.png"));

                s.setMaterial(mat);
            }

            e.getViewComponent().addChild(shape);

            if (isAnimated) {
                animationBuilder()
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .delay(Duration.seconds(x * 0.5 + 1.0))
                        .repeatInfinitely()
                        .autoReverse(true)
                        .translate(e)
                        .from(e.getPosition3D())
                        .to(e.getPosition3D().add(0, -2, 0))
                        .buildAndPlay();

                animationBuilder()
                        //.interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                        .duration(Duration.seconds(2.0))
                        .delay(Duration.seconds(x * 0.5 + 1.0))
                        .repeatInfinitely()
                        .autoReverse(true)
                        .rotate(e)
                        .from(new Point3D(0, 0, 0))
                        .to(new Point3D(360, 0, 0))
                        .buildAndPlay();
            }

            x++;
        }

        entityBuilder()
                .buildOrigin3DAndAttach();

        // light

        newLight(25, -2, -4);
        newLight(-10, -4, 0);
    }

    private Node[] makeShapes() {
        var separator = new Box(0.4, 3, 4);
        separator.setVisible(false);

        //

        var combinedPyramid = new Model3D();
        var p1 = new Pyramid(1, 0, 1, 4);
        var p2 = new Pyramid(1, 0, 1, 4);

        p1.setTranslateY(-0.5);
        p2.setTranslateY(0.5);
        p2.setRotate(180);

        combinedPyramid.addMeshView(p1);
        combinedPyramid.addMeshView(p2);

        //

        var combinedCone = new Model3D();
        var c1 = new Cone(1, 0, 1);
        var c2 = new Torus();
        var c3 = new Torus();

        c1.setTranslateY(-0.5);

        c2.setTranslateY(0.25);
        c2.setRotationAxis(Rotate.X_AXIS);
        c2.setRotate(90);

        c3.setTranslateY(0.75);
        c3.setRotationAxis(Rotate.X_AXIS);
        c3.setRotate(90);

        combinedCone.addMeshView(c1);
        combinedCone.addMeshView(c2);
        combinedCone.addMeshView(c3);

        //

        var combinedTorus = new Model3D();
        var cyl = new Cylinder(1, 0.5, 1.5);
        cyl.setTranslateY(0.25);

        var tor = new Torus(0.3);
        tor.setTranslateY(-0.5);

        combinedTorus.addMeshView(cyl);
        combinedTorus.addMeshView(tor);

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
                new Cone(),

                separator,

                combinedPyramid,
                combinedCone,
                combinedTorus
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
