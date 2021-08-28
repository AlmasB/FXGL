/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameSubScene;
import com.almasb.fxgl.scene3d.Cuboid;
import javafx.geometry.Point3D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EntitiesScenesSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            pushNewGameSubScene();
        });
    }

    private void pushNewGameSubScene() {
        // 3D = true
        var scene = new GameSubScene(getAppWidth(), getAppHeight(), true);

        var text = getUIFactoryService().newText("Sub Scene", Color.BLACK, 54);
        text.setTranslateX(250);
        text.setTranslateY(250);
        text.setEffect(new DropShadow(5, 7.5, 3.5, Color.BLACK));

        var player = entityBuilder()
                .at(0, 0, -5)
                .view(new Cuboid(1, 1, 1))
                .build();

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .duration(Duration.seconds(2))
                .repeatInfinitely()
                .autoReverse(true)
                .translate(player)
                .from(new Point3D(-3, 1, -11))
                .to(new Point3D(4, 0, 0))
                .buildAndPlay(scene);

        scene.getGameScene().setBackgroundColor(Color.web("green", 0.3));
        scene.getGameScene().addUINode(text);
        scene.getGameWorld().addEntities(player);

        getSceneService().pushSubScene(scene);

        onKeyBuilder(scene.getInput(), KeyCode.F)
                .onActionBegin(() -> pushNewGameSubScene());

        onKeyBuilder(scene.getInput(), KeyCode.G)
                .onActionBegin(() -> getSceneService().popSubScene());
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .at(150, 150)
                .view(getUIFactoryService().newText("Normal Game Scene", Color.BLACK, 74))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
