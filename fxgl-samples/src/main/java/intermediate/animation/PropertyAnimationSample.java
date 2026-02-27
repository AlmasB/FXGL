/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.animation;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to animate JavaFX properties.
 */
public class PropertyAnimationSample extends GameApplication {

    private List<Runnable> animations = new ArrayList<>();
    private int animationIndex = 0;
    
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidthFromRatio(16/9.0);
    }
    
    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            nextAnimation(animationIndex++);
        });
    }

    private void nextAnimation(int index) {
        if (index < animations.size())
            animations.get(index).run();
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        var shadow = new DropShadow(25, Color.YELLOW);

        // the JavaFX object whose properties we'll animate
        var rect = new Rectangle(100, 50, Color.color(0, 0.6, 0.3, 0.5).brighter().brighter());
        rect.setArcWidth(35);
        rect.setArcHeight(35);
        rect.setStroke(Color.color(0.2, 0.72, 0.18, 0.75));
        rect.setStrokeWidth(5.5);
        rect.setEffect(shadow);

        addUINode(rect, 125, 100);

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                        .animate(rect.widthProperty())
                        .from(100)
                        .to(800)::buildAndPlay
        );

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.BOUNCE.EASE_OUT())
                        .animate(rect.heightProperty())
                        .from(50)
                        .to(400)::buildAndPlay
        );

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(1.5))
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .animate(rect.arcWidthProperty())
                        .from(35)
                        .to(500)::buildAndPlay
        );
        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(1.5))
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .animate(rect.arcHeightProperty())
                        .from(35)
                        .to(500)::buildAndPlay
        );

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.QUINTIC.EASE_OUT())
                        .animate(rect.fillProperty())
                        .to(Color.color(0.1, 0.0, 0.8, 0.58))::buildAndPlay
        );

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.BACK.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .rotate(rect)
                        .from(0)
                        .to(360)::buildAndPlay
        );

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.ELASTIC.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .scale(rect)
                        .from(new Point2D(1, 1))
                        .to(new Point2D(1.25, 1.25))::buildAndPlay
        );

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(1.2))
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .animate(shadow.offsetXProperty())
                        .from(0)
                        .to(150)::buildAndPlay
        );

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(1.2))
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .animate(shadow.offsetYProperty())
                        .from(0)
                        .to(150)::buildAndPlay
        );

        animations.add(
                animationBuilder()
                        .duration(Duration.seconds(0.65))
                        .interpolator(Interpolators.BOUNCE.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .animate(shadow.radiusProperty())
                        .from(25)
                        .to(255)::buildAndPlay
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
