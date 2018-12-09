/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.DSLKt.*;

/**
 * Shows how to use input service and bind actions to triggers.
 */
public class AnimSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AnimSample");
        settings.setVersion("0.1");
    }

    private Animation<?> anim;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "f", () -> anim.start());
        onKeyDown(KeyCode.G, "g", () -> anim.stop());
        onKeyDown(KeyCode.Q, "q", () -> anim.pause());
        onKeyDown(KeyCode.E, "e", () -> anim.resume());
    }

    @Override
    protected void initGame() {

        Entity e = entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach();

        Entity e2 = entityBuilder()
                .at(100, 150)
                .view(new Rectangle(40, 40, Color.RED))
                .buildAndAttach();

        anim = translateAnim(e, new Point2D(200, 100), Duration.seconds(1));

        translate(e2, new Point2D(200, 150), Duration.seconds(2));
    }

    @Override
    protected void onUpdate(double tpf) {
        anim.onUpdate(tpf);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
