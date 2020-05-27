/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleMorphApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidthFromRatio(16/9.0);
        settings.setManualResizeEnabled(true);
        //settings.setProfilingEnabled(true);
    }

    private List<Rectangle> pixels;
    private List<Rectangle> pixels2;

    double delayIndex = 0;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            delayIndex = 0.0;

            pixels.stream()
                    .sorted(Comparator.comparingDouble(p -> p.getLayoutY()))
                    .forEach(p -> {
                        //p.setBlendMode(BlendMode.ADD);

                        animationBuilder()
                                .duration(Duration.seconds(0.2))
                                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                                .onFinished(() -> {
                                    animationBuilder()
                                            .delay(Duration.seconds(random(0.1, 0.6)))
                                            .onFinished(() -> {
                                                animationBuilder()
                                                        .delay(Duration.seconds(random(delayIndex, delayIndex + 0.1)))
                                                        .duration(Duration.seconds(1.5))
                                                        .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                                                        .translate(p)
                                                        .alongPath(new CubicCurve(p.getTranslateX(), p.getTranslateY(),
                                                                random(200, 300), random(-200, 20),
                                                                random(600, 700), random(500, 700),
                                                                650, 150))
                                                        .buildAndPlay();

                                                delayIndex += 0.0001;
                                            })
                                            .duration(Duration.seconds(0.75))
                                            .interpolator(Interpolators.BOUNCE.EASE_OUT())
                                            .animate(new AnimatedValue<>(0.0, 1.0))
                                            .onProgress(progress -> {
                                                var x = p.getTranslateX();
                                                var y = p.getTranslateY();

                                                var noiseValue = FXGLMath.noise2D(x * 0.002 * progress, y * 0.002 * t);
                                                var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);

                                                angle %= 360.0;

                                                var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 25));

                                                Vec2 velocity = (Vec2) p.getProperties().get("vel");

                                                var vx = velocity.x * 0.8f + v.x * 0.2f;
                                                var vy = velocity.y * 0.8f + v.y * 0.2f;

                                                velocity.x = vx;
                                                velocity.y = vy;

                                                p.setTranslateX(x + velocity.x);
                                                p.setTranslateY(y + velocity.y);
                                            })
                                            .buildAndPlay();
                                })
                                .scale(p)
                                .from(new Point2D(1, 1))
                                .to(new Point2D(3, 3))
                                .buildAndPlay();
                    });
        });

        onKeyDown(KeyCode.G, () -> {
            morph();
        });
    }

    private int pixelIndex = 0;

    @Override
    protected void initGame() {
        pixelIndex = 0;

        getGameScene().setBackgroundColor(Color.BLACK);

        var texture = texture("robot_stand.png").subTexture(new Rectangle2D(50, 30, 200, 220));

        pixels = ImagesKt.toPixels(texture.getImage())
                .stream()
                //.filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .map(p -> {
                    var r = new Rectangle(1, 1, p.getColor());
                    r.setLayoutX(p.getX());
                    r.setLayoutY(p.getY());
                    r.setScaleX(3);
                    r.setScaleY(3);
                    return r;
                })
                .collect(Collectors.toList());

        pixels.forEach(p -> {
            p.getProperties().put("vel", new Vec2());
            p.getProperties().put("index", pixelIndex++);
            addUINode(p, 250, 150);
        });

        var knight = texture("knight.png", 200, 220);

        pixels2 = ImagesKt.toPixels(knight.getImage())
                .stream()
                //.filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .map(p -> {
                    var r = new Rectangle(1, 1, p.getColor());
                    r.setLayoutX(p.getX());
                    r.setLayoutY(p.getY());
                    return r;
                })
                .collect(Collectors.toList());

//        pixels2.forEach(p -> {
//            addUINode(p, 450, 150);
//        });

        System.out.println(pixels.size() + " "  + pixels2.size());
    }

    private void morph() {
        delayIndex = 0.0;

        pixels.stream()
                .filter(p -> {
                    int pIndex = (int) p.getProperties().get("index");

                    Rectangle p2 = pixels2.get(pIndex);

                    return !(p.getFill().equals(Color.TRANSPARENT) && p2.getFill().equals(Color.TRANSPARENT));
                })
                .sorted(Comparator.comparingDouble(p -> p.getLayoutY()))
                .forEach(p -> {
                    int pIndex = (int) p.getProperties().get("index");

                    Rectangle p2 = pixels2.get(pIndex);

                    animationBuilder()
                            .delay(Duration.seconds(random(delayIndex, delayIndex + 0.1)))
                            .duration(Duration.seconds(1.5))
                            .interpolator(Interpolators.BOUNCE.EASE_IN())
                            .translate(p)
                            .alongPath(new CubicCurve(p.getTranslateX(), p.getTranslateY(),
                                    random(200, 300), random(-200, 20),
                                    random(600, 700), random(500, 700),
                                    650 - p.getLayoutX() + p2.getLayoutX(), 150 - p.getLayoutY() + p2.getLayoutY()))
                            .buildAndPlay();

                    animationBuilder()
                            .delay(Duration.seconds(random(delayIndex, delayIndex + 0.1)))
                            .duration(Duration.seconds(1.5))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                            .animate(p.fillProperty())
                            .from(p.getFill())
                            .to(p2.getFill())
                            .buildAndPlay();

                    delayIndex += 0.0001;
                });
    }

    double t = 0.0;
    boolean up = true;

    @Override
    protected void onUpdate(double tpf) {
        if (up) {
            t += tpf;
        } else {
            t -= tpf;
        }

        if (t > 7) {
            up = false;
        }

        if (t < 1) {
            up = true;
        }

        // perlin noise
//        pixels.forEach(p -> {
//            var x = p.getTranslateX();
//            var y = p.getTranslateY();
//
//            var noiseValue = FXGLMath.noise2D(x * 0.002 * t, y * 0.002 * t);
//            var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);
//
//            angle %= 360.0;
//
//            var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 25));
//
//            Vec2 velocity = (Vec2) p.getProperties().get("vel");
//
//            var vx = velocity.x * 0.8f + v.x * 0.2f;
//            var vy = velocity.y * 0.8f + v.y * 0.2f;
//
//            velocity.x = vx;
//            velocity.y = vy;
//
//            p.setTranslateX(x + velocity.x);
//            p.setTranslateY(y + velocity.y);
//        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
