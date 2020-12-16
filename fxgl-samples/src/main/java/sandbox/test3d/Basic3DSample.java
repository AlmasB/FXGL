/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Basic3DSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Basic 3D Sample");
        settings.setExperimental3D(true);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKCYAN);

        var box = new Box(1, 1, 1);
        box.setMaterial(new PhongMaterial(Color.BLUE));

        var player = entityBuilder()
                .at(0, 0, -5)
                .view(box)
                .buildAndAttach();

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .duration(Duration.seconds(2))
                .repeatInfinitely()
                .autoReverse(true)
                .translate(player)
                .from(new Point3D(-3, 1, -5))
                .to(new Point3D(3, 0, 0))
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
