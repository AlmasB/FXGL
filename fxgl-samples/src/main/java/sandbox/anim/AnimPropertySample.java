/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.anim;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class AnimPropertySample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Anim Property Sample");
        settings.setWidthFromRatio(16/9.0);
    }

    private int i = 0;
    private Map<Integer, Runnable> anims = new HashMap<>();

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            nextAnimation(i++);
        });
    }

    private void nextAnimation(int index) {
        if (index < anims.size())
            anims.get(index).run();
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        var shadow = new DropShadow(25, Color.YELLOW);

        var rect = new Rectangle(100, 50, Color.color(0, 0.6, 0.3, 0.5).brighter().brighter());
        rect.setArcWidth(35);
        rect.setArcHeight(35);
        rect.setStroke(Color.color(0.2, 0.72, 0.18, 0.75));
        rect.setStrokeWidth(5.5);
        rect.setEffect(shadow);

        addUINode(rect, 125, 100);

        anims.put(0,
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                        .animate(rect.widthProperty())
                        .from(100)
                        .to(800)::buildAndPlay
        );

        anims.put(1,
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.BOUNCE.EASE_OUT())
                        .animate(rect.heightProperty())
                        .from(50)
                        .to(400)::buildAndPlay
        );

        anims.put(2,
                animationBuilder()
                        .duration(Duration.seconds(1.5))
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .animate(rect.arcWidthProperty())
                        .from(35)
                        .to(500)::buildAndPlay
        );
        anims.put(3,
                animationBuilder()
                        .duration(Duration.seconds(1.5))
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .animate(rect.arcHeightProperty())
                        .from(35)
                        .to(500)::buildAndPlay
        );

        anims.put(4,
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.QUINTIC.EASE_OUT())
                        .animate(rect.fillProperty())
                        .to(Color.color(0.1, 0.0, 0.8, 0.58))::buildAndPlay
        );

        anims.put(5,
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.BACK.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .rotate(rect)
                        .from(0)
                        .to(360)::buildAndPlay
        );

        anims.put(6,
                animationBuilder()
                        .duration(Duration.seconds(2))
                        .interpolator(Interpolators.ELASTIC.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .scale(rect)
                        .from(new Point2D(1, 1))
                        .to(new Point2D(1.25, 1.25))::buildAndPlay
        );

        anims.put(7,
                animationBuilder()
                        .duration(Duration.seconds(1.2))
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .animate(shadow.offsetXProperty())
                        .from(0)
                        .to(150)::buildAndPlay
        );

        anims.put(8,
                animationBuilder()
                        .duration(Duration.seconds(1.2))
                        .interpolator(Interpolators.SMOOTH.EASE_OUT())
                        .repeat(2)
                        .autoReverse(true)
                        .animate(shadow.offsetYProperty())
                        .from(0)
                        .to(150)::buildAndPlay
        );

        anims.put(9,
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
