/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.customization;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.PauseMenu;
import com.almasb.fxgl.app.SceneFactory;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;
import com.almasb.fxgl.ui.FontType;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sandbox.MyEntityFactory;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomPauseMenuApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public PauseMenu newPauseMenu() {
                return new MyPauseMenu();
            }
        });
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new MyEntityFactory());

        Level level = FXGL.getAssetLoader().loadLevel("test_level.txt", new TextLevelLoader(30, 30, '0'));

        FXGL.getGameWorld().setLevel(level);

        FXGL.spawn("rect", 750, 550);
    }

    public static class MyPauseMenu extends PauseMenu {

        private static final int SIZE = 150;

        private Animation<?> animation;

        public MyPauseMenu() {
            getContentRoot().setTranslateX(FXGL.getAppWidth() / 2.0 - SIZE);
            getContentRoot().setTranslateY(FXGL.getAppHeight() / 2.0 - SIZE);

            var shape = Shape.subtract(new Circle(SIZE, SIZE, SIZE), new Rectangle(0, SIZE, SIZE*2, SIZE));

            var shape2 = Shape.subtract(shape, new Rectangle(0, 0, SIZE, SIZE));

            shape = Shape.subtract(shape, new Rectangle(SIZE, 0, SIZE, SIZE));

            shape.setStrokeWidth(2.5);
            shape.strokeProperty().bind(
                    Bindings.when(shape.hoverProperty()).then(Color.YELLOW).otherwise(Color.BLACK)
            );

            shape.fillProperty().bind(
                    Bindings.when(shape.pressedProperty()).then(Color.YELLOW).otherwise(Color.color(0.1, 0.05, 0.0, 0.75))
            );

            shape.setOnMouseClicked(e -> requestHide());

            shape2.setStrokeWidth(2.5);
            shape2.strokeProperty().bind(
                    Bindings.when(shape2.hoverProperty()).then(Color.YELLOW).otherwise(Color.BLACK)
            );

            shape2.fillProperty().bind(
                    Bindings.when(shape2.pressedProperty()).then(Color.YELLOW).otherwise(Color.color(0.1, 0.05, 0.0, 0.75))
            );
            shape2.setOnMouseClicked(e -> FXGL.getGameController().exit());

            var shape3 = new Rectangle(SIZE*2, SIZE / 2);
            shape3.setStrokeWidth(2.5);
            shape3.strokeProperty().bind(
                    Bindings.when(shape3.hoverProperty()).then(Color.YELLOW).otherwise(Color.BLACK)
            );

            shape3.fillProperty().bind(
                    Bindings.when(shape3.pressedProperty()).then(Color.YELLOW).otherwise(Color.color(0.1, 0.05, 0.0, 0.75))
            );

            shape3.setTranslateY(SIZE);

            Text textResume = FXGL.getUIFactory().newText("RESUME", Color.WHITE, 24.0);
            textResume.setFont(FXGL.getUIFactory().newFont(FontType.GAME, 24.0));
            textResume.setTranslateX(50);
            textResume.setTranslateY(100);
            textResume.setMouseTransparent(true);

            Text textExit = FXGL.getUIFactory().newText("EXIT", Color.WHITE, 24.0);
            textExit.setFont(FXGL.getUIFactory().newFont(FontType.GAME, 24.0));
            textExit.setTranslateX(200);
            textExit.setTranslateY(100);
            textExit.setMouseTransparent(true);

            Text textOptions = FXGL.getUIFactory().newText("OPTIONS", Color.WHITE, 24.0);
            textOptions.setFont(FXGL.getUIFactory().newFont(FontType.GAME, 24.0));
            textOptions.setTranslateX(110);
            textOptions.setTranslateY(195);
            textOptions.setMouseTransparent(true);

            getContentRoot().getChildren().addAll(shape, shape2, shape3, textResume, textExit, textOptions);

            getContentRoot().setScaleX(0);
            getContentRoot().setScaleY(0);

            animation = FXGL.animationBuilder()
                    .duration(Duration.seconds(0.66))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .scale(getContentRoot())
                    .from(new Point2D(0, 0))
                    .to(new Point2D(1, 1))
                    .build();
        }

        @Override
        public void onCreate() {
            animation.setOnFinished(EmptyRunnable.INSTANCE);
            animation.start();
        }

        @Override
        protected void onUpdate(double tpf) {
            animation.onUpdate(tpf);
        }

        @Override
        protected void onHide() {
            if (animation.isAnimating())
                return;

            animation.setOnFinished(() -> FXGL.getGameController().popSubScene());
            animation.startReverse();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
