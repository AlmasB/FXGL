/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use interpolators.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InterpolatorSample2 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setHeight(700);
    }

    @Override
    protected void initGame() {
        int i = 0;

        for (var interpolator : Interpolators.values()) {
            Text t = getUIFactoryService().newText(interpolator.toString() + ":");
            t.setFill(Color.BLACK);

            Pane p = new Pane(t);
            p.setTranslateY(i * 50 + 25);

            Line line = new Line(0, i * 50, getAppWidth(), i * 50);
            line.setStroke(Color.RED);

            getGameScene().addUINodes(p, line);

            Entity bird = entityBuilder()
                    .at(70, i * 50)
                    .view(texture("bird.png").toAnimatedTexture(2, Duration.seconds(0.5)).loop())
                    .buildAndAttach();

            animationBuilder()
                    .interpolator(interpolator.EASE_OUT())
                    .duration(Duration.seconds(2))
                    .repeatInfinitely()
                    .translate(bird)
                    .from(new Point2D(120, i * 50))
                    .to(new Point2D(getAppWidth() - 70, i * 50))
                    .buildAndPlay();

            i++;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
