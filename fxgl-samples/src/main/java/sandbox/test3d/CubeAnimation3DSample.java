/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene3d.Cuboid;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.util.Duration;

import java.util.Comparator;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CubeAnimation3DSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.L, () -> getGameController().exit());
    }

    private int delayIndex = 0;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKGRAY);

        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        var cube = new Group();

        var size = 0.3;

        for (int z = -3; z <= 3; z++) {
            for (int y = -3; y <= 3; y++) {
                for (int x = -3; x <= 3; x++) {
                    var smallCube = new Cuboid(size, size, size);
                    smallCube.setPhongMaterial(Color.GOLD);
                    smallCube.setTranslation(new Point3D(x * size, y * size, z * size));

                    cube.getChildren().add(smallCube);
                }
            }
        }

        cube.getChildren()
                .stream()
                .sorted(Comparator.comparingDouble(c ->
                        Math.abs(c.getTranslateX())
                                + Math.abs(c.getTranslateY())
                                + Math.abs(c.getTranslateZ()))
                )
                .forEach(c -> {
                    animationBuilder()
                            .interpolator(Interpolators.BOUNCE.EASE_OUT())
                            .delay(Duration.seconds(delayIndex * 0.005))
                            .duration(Duration.seconds(2))
                            .autoReverse(true)
                            .repeatInfinitely()
                            .scale(c)
                            .from(new Point3D(1, 1, 1))
                            .to(new Point3D(1.5, 1.5, 1.5))
                            .buildAndPlay();

                    var mat = (PhongMaterial) ((Cuboid) c).getMaterial();

                    animationBuilder()
                            .delay(Duration.seconds(delayIndex * 0.005))
                            .duration(Duration.seconds(2))
                            .autoReverse(true)
                            .repeatInfinitely()
                            .animate(mat.diffuseColorProperty())
                            .from(Color.GOLD)
                            .to(Color.BLUE)
                            .buildAndPlay();

                    delayIndex++;
                });

        var e = entityBuilder()
                .at(0, 2, -8)
                .view(cube)
                .buildAndAttach();

        animationBuilder()
                .duration(Duration.seconds(4))
                .repeatInfinitely()
                .rotate(e)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(0, 360, 0))
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
