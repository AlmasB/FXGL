/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CirclesSample extends GameApplication {

    private List<Interpolator> interpolators = new ArrayList<>();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);

            for (int i = 0; i < interpolators.size(); i++) {

                var interpolator = interpolators.get(i);

                var box = new Box(1, 1, 1);
                box.setMaterial(new PhongMaterial(Color.BLUE));

                var c = entityBuilder()
                        .view(box)
                        .buildAndAttach();

                var center = new Point2D(0, 0);
                var point = new Point2D(2 + i * 1, 0.0).add(center);

                animationBuilder()
//                        .repeat(2)
//                        .autoReverse(true)
                        .duration(Duration.seconds(8))
                        .interpolator(interpolator)
                        .animate(new AnimatedValue<>(0.0, 360.0))
                        .onProgress(deg -> {
                            var p = FXGLMath.rotate(point, center, deg);

                            c.setX(p.getX());
                            c.setY(p.getY());
                        })
                        .buildAndPlay();
            }
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        var t = getGameScene().getCamera3D().getTransform();
        t.translateZ(-10);
        t.translateY(-15);
        t.translateX(45);
        t.lookAt(new Point3D(0, 0, 0));

        for (int i = 0; i < Interpolators.values().length - 2; i++) {
            interpolators.add(Interpolators.values()[i].EASE_OUT());
            interpolators.add(Interpolators.values()[i].EASE_IN());
            interpolators.add(Interpolators.values()[i].EASE_IN_OUT());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
