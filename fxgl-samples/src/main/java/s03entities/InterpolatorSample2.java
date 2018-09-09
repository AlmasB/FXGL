/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Shows how to use interpolators.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InterpolatorSample2 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(600);
        settings.setHeight(700);
        settings.setTitle("InterpolatorSample2");
    }

    @Override
    protected void initGame() {

        int i = 0;

        for (Interpolators interpolator : Interpolators.values()) {

            Text t = getUIFactory().newText(interpolator.toString() + ":");
            t.setFill(Color.BLACK);

            Pane p = new Pane(t);
            p.setTranslateY(i * 50 + 25);

            Line line = new Line(0, i * 50, getWidth(), i * 50);
            line.setStroke(Color.RED);

            getGameScene().addUINodes(p, line);

            Texture texture = DSLKt.texture("bird.png").toAnimatedTexture(2, Duration.seconds(0.5));

            Entity bird = Entities.builder()
                    .at(100, i * 50)
                    .viewFromNode(texture)
                    .buildAndAttach();

            Entities.animationBuilder()
                    .interpolator(interpolator.EASE_OUT())
                    .duration(Duration.seconds(2))
                    .repeatInfinitely()
                    .translate(bird)
                    .from(new Point2D(100, i * 50))
                    .to(new Point2D(400, i * 50))
                    .buildAndPlay();

            i++;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
