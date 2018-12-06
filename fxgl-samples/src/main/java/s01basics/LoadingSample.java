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
import javafx.geometry.Point2D;
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
    protected void initPhysics() {
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

        private List<Animation<?>> animations = new ArrayList<>();

        public MyLoadingScene() {

            getContentRoot().getChildren().clear();

            for (int i = 0; i < 3; i++) {
                Rectangle r = new Rectangle(40, 40);

                r.setTranslateX(FXGLMath.random(FXGL.getAppWidth()));
                r.setTranslateY(FXGLMath.random(2));

                var a = DSLKt.translate(r,
                        new Point2D(r.getTranslateX(), r.getTranslateY()),
                        new Point2D(FXGLMath.random(FXGL.getAppWidth()), FXGL.getAppHeight()),
                        Duration.seconds(4.5));

                a.getAnimatedValue().setInterpolator(Interpolators.ELASTIC.EASE_OUT());

                animations.add(a);

                getContentRoot().getChildren().addAll(r);
            }

            animations.forEach(a -> a.start());
        }

        @Override
        protected void onUpdate(double tpf) {

            super.onUpdate(tpf);

            animations.forEach(a -> a.onUpdate(tpf));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
