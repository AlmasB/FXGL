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

            getContentRoot().getChildren().addAll(new Rectangle(getAppWidth(), getAppHeight(), Color.LIGHTGRAY));

            var largeCircle = new Circle(45);
            largeCircle.setTranslateX(getAppWidth() / 2.0);
            largeCircle.setTranslateY(getAppHeight() / 3.0);

            for (int i = 0; i < 3; i++) {
                var circle = new Circle(10, 10, 10, Color.BLUE);
                circle.setTranslateX(getAppWidth() / 2.0);
                circle.setTranslateY(getAppHeight() / 3.0);

                getContentRoot().getChildren().addAll(circle);

                animationBuilder(this)
                        .delay(Duration.seconds(i * 0.4))
                        .duration(Duration.seconds(1.6))
                        .repeatInfinitely()
                        .interpolator(Interpolators.SMOOTH.EASE_IN_OUT())
                        .translate(circle)
                        .alongPath(largeCircle)
                        .buildAndPlay();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
