/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.Pixel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.texture.ImagesKt.toImage;
import static com.almasb.fxgl.texture.ImagesKt.toPixels;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleDigitsSample extends GameApplication {

    private static final String DIGITS_STRING = "0123456789";

    private static final Point2D ANIMATION_OFFSET = new Point2D(560, 200);

    private List<ParticleDigit> digits = new ArrayList<>();

    private List<Shape> particles = new ArrayList<>();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        initParticles();

        IntegerProperty index = new SimpleIntegerProperty();

        run(() -> {

            animateDigit(index.get());

            index.set(index.get() + 1);

            if (index.get() == DIGITS_STRING.length())
                index.set(0);

        }, Duration.seconds(3));
    }

    private void initParticles() {
        // blur particle pane to smooth out edges
        var particlePane = new Pane();
        particlePane.setEffect(new BoxBlur(2, 2, 3));

        addUINode(particlePane);

        int max = 0;

        for (char c : DIGITS_STRING.toCharArray()) {
            // shape of each digit
            var text = getUIFactoryService().newText(c + "", Color.WHITE, 256);

            var digitPixels = toPixels(toImage(text))
                    .stream()
                    .filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                    .collect(Collectors.toList());

            max = Math.max(max, digitPixels.size());

            digits.add(new ParticleDigit(digitPixels));
        }

        for (int i = 0; i < max; i++) {
            // shape of each particle
            var particle = new Circle(3, 3, 3, Color.WHITE);

            particles.add(particle);

            particlePane.getChildren().add(particle);
        }
    }

    private void animateDigit(int index) {
        List<Pixel> pixels = sort(digits.get(index));

        for (int i = 0; i < particles.size(); i++) {
            var p = particles.get(i);

            Pixel data = (i < pixels.size()) ? pixels.get(i) : pixels.get(pixels.size() - 1);

            // drives the animation
            animationBuilder()
                    .delay(Duration.seconds(i * 0.0001))
                    .interpolator(Interpolators.SMOOTH.EASE_OUT())
                    .translate(p)
                    .alongPath(new CubicCurve(
                            p.getTranslateX(), p.getTranslateY(),

                            getAppWidth() / 2.0 , getAppHeight() / 2.0,
                            getAppWidth() / 2.0 , getAppHeight(),

                            data.getX() + ANIMATION_OFFSET.getX(), data.getY() + ANIMATION_OFFSET.getY()
                    ))
                    .buildAndPlay();
        }
    }

    /**
     * Drives the order of particles being animated.
     * Try sorting by x, y, or random (shuffle).
     */
    private List<Pixel> sort(ParticleDigit particleDigit) {
        return particleDigit.pixels
                .stream()
                .sorted(Comparator.comparingDouble(t -> -t.getY()))
                .collect(Collectors.toList());
    }

    private static class ParticleDigit {
        List<Pixel> pixels;

        ParticleDigit(List<Pixel> pixels) {
            this.pixels = pixels;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
