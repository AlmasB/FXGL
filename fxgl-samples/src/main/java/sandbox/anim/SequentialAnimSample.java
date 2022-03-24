/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.anim;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to build sequential animations.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SequentialAnimSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    private Animation<?> anim;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.G, () -> anim.stop());
        onKeyDown(KeyCode.Q, () -> anim.pause());
        onKeyDown(KeyCode.E, () -> anim.resume());
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        Entity e = entityBuilder()
                .at(100, 100)
                .view("brick.png")
                .buildAndAttach();

        var anim0 = animationBuilder()
                .interpolator(Interpolators.BACK.EASE_OUT())
                .duration(Duration.seconds(2))
                .translate(e)
                .from(new Point2D(100, 100))
                .to(new Point2D(800, 100))
                .build();

        var anim1 = animationBuilder()
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .duration(Duration.seconds(1))
                .scale(e)
                .origin(new Point2D(32, 32))
                .from(new Point2D(1, 1))
                .to(new Point2D(3, 3))
                .build();

        var anim2 = animationBuilder()
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .duration(Duration.seconds(1))
                .translate(e)
                .from(new Point2D(800, 100))
                .to(new Point2D(800, 600))
                .build();

        var anim3 = animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .duration(Duration.seconds(1.5))
                .translate(e)
                .from(new Point2D(800, 600))
                .to(new Point2D(600, 300))
                .build();

        var anim4 = animationBuilder()
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .duration(Duration.seconds(2))
                .rotate(e)
                .origin(new Point2D(32, 32))
                .from(0)
                .to(360)
                .build();

        anim = animationBuilder().buildSequence(
                anim0,
                anim1,
                anim2,
                anim3,
                anim4
        );

        //anim.setInterpolator(Interpolators.EXPONENTIAL.EASE_IN());
        anim.setCycleCount(2);
        anim.setAutoReverse(true);

        anim.start();
    }

    @Override
    protected void onUpdate(double tpf) {
        anim.onUpdate(tpf);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
