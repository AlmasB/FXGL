/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to provide a custom loading scene.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class CustomLoadingSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public LoadingScene newLoadingScene() {
                return new MyLoadingScene();
            }
        });
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.L, () -> {
            getGameController().gotoLoading(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    protected void initGame() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class MyLoadingScene extends LoadingScene {

        public MyLoadingScene() {

            var circle = new Circle(10, 10, 10, Color.BLUE);
            circle.setTranslateX(getAppWidth() / 2.0);
            circle.setTranslateY(getAppHeight() / 3.0);

            var largeCircle = new Circle(100);
            largeCircle.setTranslateX(getAppWidth() / 2.0);
            largeCircle.setTranslateY(getAppHeight() / 3.0);

            animationBuilder(this)
                    .duration(Duration.seconds(2.5))
                    .repeatInfinitely()
                    .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                    .translate(circle)
                    .alongPath(largeCircle)
                    .buildAndPlay();

            getContentRoot().getChildren().addAll(new Rectangle(getAppWidth(), getAppHeight(), Color.LIGHTGRAY), circle);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
