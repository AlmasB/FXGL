/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.scene3d;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.scene3d.Cuboid;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to create a simple 3D cube and use it for the player entity view.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Basic3DSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKCYAN);

        var cube = new Cuboid(1, 1, 1);
        cube.setPhongMaterial(Color.BLUE);

        var player = entityBuilder()
                .at(0, 0, -5)
                .view(cube)
                .buildAndAttach();

        // animate player entity
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
