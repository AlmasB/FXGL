/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HappyNewYearApp extends GameApplication {

    private static final String MESSAGE = "Happy New Year\n          2023!";

    private List<SnowParticle> particles;

    private boolean[][] points = new boolean[1280][720];

    private double t = 0.0;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.D, () -> {
            t = 0.0;

            particles.forEach(p -> {
                p.isCollidable = false;

                if (!p.isRunning) {
                    animationBuilder()
                            .onFinished(() -> {
                                animationBuilder()
                                        .duration(Duration.seconds(3))
                                        .interpolator(Interpolators.BOUNCE.EASE_IN())
                                        .fadeOut(p)
                                        .buildAndPlay();
                            })
                            .duration(Duration.seconds(2 + t * 0.02))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                            .delay(Duration.seconds(t * 0.07))
                            .scale(p)
                            .to(new Point2D(3, 3))
                            .buildAndPlay();

                    t += 0.01;
                }
            });
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setCursorInvisible();
        getGameScene().setBackgroundColor(Color.BLACK);

        particles = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            runOnce(() -> {
                var p = new SnowParticle();
                p.setTranslateX(random(5, getAppWidth() - 5));

                particles.add(p);

                addUINode(p);
            }, Duration.millis(i));
        }

        Text text = getUIFactoryService().newText(MESSAGE, Color.BLACK, 122);
        ImagesKt.toPixels(ImagesKt.toImage(text))
                .stream()
                .filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .forEach(p -> {
                    points[p.getX() + 165][p.getY() + 250] = true;
                });
    }

    @Override
    protected void onUpdate(double tpf) {
        particles.forEach(p -> {
            if (p.isRunning) {
                p.setTranslateY(p.getTranslateY() + random(1.0, 15));

                if (p.getTranslateY() >= 715) {
                    p.setTranslateX(random(5, getAppWidth() - 5));
                    p.setTranslateY(0);
                }

                if (p.isCollidable) {
                    var isCollision = points[(int) p.getTranslateX()][(int) p.getTranslateY()];

                    if (FXGLMath.randomBoolean(0.1) && isCollision) {
                        p.isRunning = false;
                    }
                }
            }
        });
    }

    private static class SnowParticle extends Circle {
        boolean isRunning = true;
        boolean isCollidable = true;

        SnowParticle() {
            setRadius(0.5);
            setFill(Color.WHITE);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
