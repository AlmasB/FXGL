/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
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
 * Shows how to convert an image into particles and then animate them.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ImageToParticlesAnimationSample extends GameApplication {

    private List<Node> pixels;

    private double delayIndex = 0;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1000);
        settings.setHeight(562);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            playAnimation();
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        // based on your image size, you may want to remove the call to .subTexture()
        var texture = texture("robot_stand.png").subTexture(new Rectangle2D(50, 30, 200, 220));

        pixels = ImagesKt.toPixels(texture.getImage())
                .stream()
                .filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .map(p -> {
                    var r = new Rectangle(1, 1, p.getColor());
                    r.setLayoutX(p.getX());
                    r.setLayoutY(p.getY());

                    addUINode(r, 250, 150);
                    return r;
                })
                .collect(Collectors.toList());
    }

    private void playAnimation() {
        delayIndex = 0.0;

        pixels.stream()
                .sorted(Comparator.comparingDouble(p -> p.getLayoutY()))
                .forEach(p -> {
                    animationBuilder()
                            .duration(Duration.seconds(0.2))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                            .onFinished(() -> {
                                animationBuilder()
                                        .delay(Duration.seconds(random(delayIndex, delayIndex + 0.1)))
                                        .duration(Duration.seconds(2.5))
                                        .interpolator(Interpolators.BACK.EASE_IN_OUT())
                                        .translate(p)
                                        .alongPath(
                                                new CubicCurve(
                                                        p.getTranslateX(), p.getTranslateY(),
                                                        random(200, 300), random(-200, 20),
                                                        random(600, 700), random(500, 700),
                                                        650, 150
                                                )
                                        )
                                        .buildAndPlay();

                                delayIndex += 0.0001;
                            })
                            .scale(p)
                            .from(new Point2D(1, 1))
                            .to(new Point2D(3, 3))
                            .buildAndPlay();
                });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
