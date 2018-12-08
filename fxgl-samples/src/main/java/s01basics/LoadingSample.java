/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.*;
import com.almasb.fxgl.core.math.FXGLMath;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        settings.setMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @NotNull
            @Override
            public LoadingScene newLoadingScene() {
                return new MyLoadingScene();
            }
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

            getContentRoot().getChildren().clear();

            r.setStroke(Color.BLACK);

            r.setTranslateX(FXGLMath.random(FXGL.getAppWidth()));
            r.setTranslateY(FXGLMath.random(2));



            getContentRoot().getChildren().addAll(r);
        }

        @Override
        public void bind(@NotNull Task<?> task) {
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
