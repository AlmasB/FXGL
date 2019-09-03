/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.IntroScene;
import com.almasb.fxgl.app.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FullscreenApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setIntroEnabled(true);
        //settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
        settings.setMenuEnabled(true);

        settings.setSceneFactory(new SceneFactory() {
            @Override
            public IntroScene newIntro() {
                return new MyIntroScene();
            }
        });
    }

    @Override
    protected void initGame() {
        FXGL.entityBuilder()
                .at(300, 300)
                .view(new Rectangle(40, 40))
                .buildAndAttach();
    }

    public static class MyIntroScene extends IntroScene {

        private static final int SIZE = 150;

        private List<Animation<?>> animations = new ArrayList<>();

        private int index = 0;

        public MyIntroScene() {
            var circle1 = new Circle(SIZE, SIZE, SIZE, Color.color(0.94, 0.5, 0.73, 0.6));
            var circle2 = new Circle(SIZE, SIZE, SIZE, Color.color(0.4, 0.5, 0.33, 0.6));
            circle2.setTranslateX(300);
            circle2.setTranslateY(250);
            var circle3 = new Circle(SIZE, SIZE, SIZE, Color.color(0.14, 0.95, 0.13, 0.6));
            circle3.setTranslateX(500);
            circle3.setTranslateY(50);

            var circles = new Group(circle1, circle2, circle3);

            circles.getChildren().forEach(c -> {
                var anim = FXGL.animationBuilder()
                        .duration(Duration.seconds(1.66))
                        .delay(Duration.seconds(1.66 * index++))
                        .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                        .scale(c)
                        .from(new Point2D(0, 0))
                        .to(new Point2D(1, 1))
                        .build();

                animations.add(anim);
            });

            animations.get(animations.size() - 1).setOnFinished(this::finishIntro);

            getContentRoot().getChildren().addAll(
                    new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight()),
                    circles
            );
        }

        @Override
        protected void onUpdate(double tpf) {
            super.onUpdate(tpf);
            animations.forEach(a -> a.onUpdate(tpf));
        }

        @Override
        public void startIntro() {
            animations.forEach(a -> a.start());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
