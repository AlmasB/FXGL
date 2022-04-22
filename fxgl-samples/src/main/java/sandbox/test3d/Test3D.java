/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.util.Duration;

import static com.almasb.fxgl.core.math.FXGLMath.cosDeg;
import static com.almasb.fxgl.core.math.FXGLMath.sinDeg;
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Test3D {

    private final Group root = new Group();

    private double t = 0.0;

    public void start() {
        getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);

        entityBuilder()
                .view(root)
                .buildAndAttach();

        demo();
    }

    private void demo() {
        var color = FXGLMath.randomColorHSB(0.66, 0.84).brighter().brighter();

        var size = 0.2;

        for (int x = 0; x < 200; x++) {
            for (int j = 0; j < 36; j++) {
                var angle = j * 360.0 / 35;

                var sphere = new Sphere(size);
                sphere.setMaterial(new PhongMaterial(color));
                sphere.setTranslateX(x * 0.12);
                sphere.setTranslateY(cosDeg(angle) * 0.8);
                sphere.setTranslateZ(sinDeg(angle) * 0.8);

                root.getChildren().add(sphere);

                var p = new Point3D(sphere.getTranslateX(), sphere.getTranslateY(), sphere.getTranslateZ());

                animationBuilder()
                        .delay(Duration.seconds(t * 0.15))
                        .duration(Duration.seconds(0.65))
                        .autoReverse(true)
                        .repeatInfinitely()
                        .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                        .rotate(sphere)
                        .origin(p.multiply(-1))
                        .from(new Point3D(0, 0, 0))
                        .to(new Point3D(angle + 15, 0, angle * 2))
                        .buildAndPlay();

                t += 0.016;
            }

            size -= 0.001;
        }
    }
}