/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BmpSample extends GameApplication {

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

        entityBuilder().at(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 55, getAppHeight() - 55)))
                .type(Type.CRYSTAL)
                .viewWithBBox(texture("ball_1.bmp", 32, 32))
                .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), 250))
                //.view(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(0.66)).loop())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
