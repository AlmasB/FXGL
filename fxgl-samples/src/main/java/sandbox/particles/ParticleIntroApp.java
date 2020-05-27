/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.geometry.Point2D;
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
public class ParticleIntroApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidthFromRatio(16/9.0);
    }

    private List<Rectangle> pixels;
    private List<Rectangle> pixels2;

    double delayIndex = 0;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            start();
        });

        onKeyDown(KeyCode.G, () -> {
            morph();
        });

        onKeyDown(KeyCode.H, () -> {
            end();
        });
    }

    private int pixelIndex = 0;

    @Override
    protected void initGame() {
        pixelIndex = 0;

        getGameScene().setBackgroundColor(Color.BLACK);

        var texture = texture("logo/fxgl_logo1.png");

        pixels = ImagesKt.toPixels(texture.getImage())
                .stream()
                .filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .map(p -> {
                    var r = new Rectangle(1, 1, p.getColor());
                    r.setLayoutX(p.getX());
                    r.setLayoutY(p.getY());
                    r.setScaleX(0);
                    r.setScaleY(0);
                    return r;
                })
                .collect(Collectors.toList());



        var knight = texture("logo/javafx_logo1.png");

        pixels2 = ImagesKt.toPixels(knight.getImage())
                .stream()
                .filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .map(p -> {
                    var r = new Rectangle(1, 1, p.getColor());
                    r.setLayoutX(p.getX() - 340);
                    r.setLayoutY(p.getY());
                    return r;
                })
                .collect(Collectors.toList());


        // add the difference in pixels as TRANSPARENT
        int numPixels = pixels.size() - pixels2.size();

        ImagesKt.toPixels(texture.getImage())
                .stream()
                .filter(p -> p.getColor().equals(Color.TRANSPARENT))
                .limit(numPixels)
                .map(p -> {
                    var r = new Rectangle(1, 1, Color.TRANSPARENT);
                    r.setLayoutX(p.getX());
                    r.setLayoutY(p.getY());
//                    r.setScaleX(3);
//                    r.setScaleY(3);
                    return r;
                })
                .forEach(pixels2::add);

        pixels.forEach(p -> {
            p.getProperties().put("vel", new Vec2());
            p.getProperties().put("index", pixelIndex++);
            addUINode(p, 300, 120);
        });

        System.out.println(pixels.size() + " "  + pixels2.size());
    }

    private void start() {
        delayIndex = 0.0;

        pixels.stream()
                .sorted(Comparator.comparingDouble(p -> p.getLayoutX()))
                .forEach(p -> {
                    animationBuilder()
                            .delay(Duration.seconds(random(delayIndex, delayIndex + 0.21)))
                            .duration(Duration.seconds(1.45))
                            .interpolator(Interpolators.ELASTIC.EASE_OUT())
                            .scale(p)
                            .from(new Point2D(0, 0))
                            .to(new Point2D(1, 1))
                            .buildAndPlay();

                    delayIndex += 0.0001;
                });
    }

    private void morph() {
        delayIndex = 0.0;

        pixels2.sort(Comparator.comparingDouble(p -> p.getLayoutX()));

        pixels.stream()
                //.parallel()
                .filter(p -> {
                    int pIndex = (int) p.getProperties().get("index");

                    Rectangle p2 = pixels2.get(pIndex);

                    return !(p.getFill().equals(Color.TRANSPARENT) && p2.getFill().equals(Color.TRANSPARENT));
                })
                .sorted(Comparator.comparingDouble(p -> p.getLayoutX()))
                .forEach(p -> {
                    int pIndex = (int) p.getProperties().get("index");

                    Rectangle p2 = pixels2.get(pIndex);

                    animationBuilder()
                            .delay(Duration.seconds(random(delayIndex, delayIndex + 0.21)))
                            .duration(Duration.seconds(1.05))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                            .translate(p)
                            .alongPath(new CubicCurve(p.getTranslateX(), p.getTranslateY(),
                                    random(200, 400), random(-150, 200),
                                    random(-300, 100), random(400, 500),
                                    650 - p.getLayoutX() + p2.getLayoutX(), 150 - p.getLayoutY() + p2.getLayoutY()))
                            .buildAndPlay();

                    animationBuilder()
                            .delay(Duration.seconds(random(delayIndex, delayIndex + 0.21)))
                            .duration(Duration.seconds(0.45))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                            .animate(p.fillProperty())
                            .from(p.getFill())
                            .to(p2.getFill())
                            .buildAndPlay();

                    delayIndex += 0.0001;
                });
    }

    private void end() {
        delayIndex = 0.0;

        pixels.stream()
                .sorted(Comparator.comparingDouble(p -> p.getLayoutX()))
                .forEach(p -> {
                    animationBuilder()
                            .delay(Duration.seconds(random(delayIndex, delayIndex + 0.21)))
                            .duration(Duration.seconds(1.45))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                            .translate(p)
                            .alongPath(new CubicCurve(p.getTranslateX(), p.getTranslateY(),
                                    random(200, 400), random(-150, 200),
                                    random(-300, 100), random(400, 500),
                                    getAppWidth() * 2, getAppHeight() * 2))
                            .buildAndPlay();

                    delayIndex += 0.0001;
                });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
