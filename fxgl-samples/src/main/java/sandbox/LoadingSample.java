/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class LoadingSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("LoadingSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMainMenuEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public LoadingScene newLoadingScene() {
                return new MyLoadingScene();
            }
        });
    }

    @Override
    protected void initInput() {
        FXGL.onKeyDown(KeyCode.L, () -> {
            FXGL.getGameController().gotoLoading(() -> {
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

        private Rectangle r = new Rectangle(40, 0, null);

        public MyLoadingScene() {
            r.setStroke(Color.BLACK);
            r.setTranslateX(FXGLMath.random(0, FXGL.getAppWidth()));
            r.setTranslateY(FXGLMath.random(0, 2));

            getContentRoot().getChildren().addAll(new Rectangle(getAppWidth(), getAppHeight(), Color.LIGHTGRAY), r);
        }

        @Override
        public void bind(Task<?> task) {
            task.progressProperty().addListener((observable, oldValue, progress) -> {
                r.setHeight(FXGL.getAppHeight() * progress.doubleValue());
            });
        }

        @Override
        protected void onUpdate(double tpf) {
            super.onUpdate(tpf);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
