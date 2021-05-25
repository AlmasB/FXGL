/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Anim3DSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.set3D(true);
    }

    @Override
    protected void initGame() {
        var box = new Box();
        box.setMaterial(new PhongMaterial(Color.BLUE));

        var e = FXGL.entityBuilder()
                .at(-3, 0, 0)
                .view(box)
                .buildAndAttach();

        FXGL.animationBuilder()
                .repeatInfinitely()
                .autoReverse(true)
                .rotate(e)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(45, 45, 45))
                .buildAndPlay();

        var box2 = new Box();
        box2.setMaterial(new PhongMaterial(Color.RED));

        FXGL.getGameScene().addGameView(new GameView(box2, 0));

        FXGL.animationBuilder()
                .repeatInfinitely()
                .autoReverse(true)
                .rotate(box2)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(45, 45, 45))
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
