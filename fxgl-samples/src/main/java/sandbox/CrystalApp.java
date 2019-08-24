/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalApp extends GameApplication {

    private enum Type {
        PLAYER, CRYSTAL
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Crystal Chase");
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        run(this::spawnCrystal, Duration.seconds(2));
    }

    private void spawnCrystal() {

        var e = entityBuilder().at(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 55, getAppHeight() - 55)))
                .type(Type.CRYSTAL)
                .view(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(0.66)).loop())
                .with(new DraggableComponent())
                .build();

        e.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            despawnWithScale(e, Duration.seconds(1), Interpolators.ELASTIC.EASE_IN());
        });

        spawnWithScale(e, Duration.seconds(1), Interpolators.ELASTIC.EASE_OUT());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
