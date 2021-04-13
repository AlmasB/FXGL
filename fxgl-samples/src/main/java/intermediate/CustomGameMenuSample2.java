/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.beans.binding.Bindings.createStringBinding;
import static javafx.beans.binding.Bindings.when;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomGameMenuSample2 extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1066);
        settings.setHeightFromRatio(16/9.0);

        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newGameMenu() {
                return new MyGameMenu();
            }
        });
    }

    public static class MyGameMenu extends FXGLMenu {

        private List<Node> buttons = new ArrayList<>();

        private int animIndex = 0;

        public MyGameMenu() {
            super(MenuType.GAME_MENU);

            var bg = texture("background.png", getAppWidth() + 450, getAppHeight() + 200);
            bg.setTranslateY(-85);
            bg.setTranslateX(-450);

            var titleView = getUIFactoryService().newText(getSettings().getTitle(), 48);
            centerTextBind(titleView, getAppWidth() / 2.0, 100);

            var body = createBody();
            body.setTranslateY(-25);

            getContentRoot().getChildren().addAll(bg, titleView, body);
        }

        @Override
        public void onCreate() {
            animIndex = 0;

            buttons.forEach(btn -> {
                btn.setOpacity(0);

                animationBuilder(this)
                        .delay(Duration.seconds(animIndex * 0.1))
                        .interpolator(Interpolators.BACK.EASE_OUT())
                        .translate(btn)
                        .from(new Point2D(-200, 0))
                        .to(new Point2D(0, 0))
                        .buildAndPlay();

                animationBuilder(this)
                        .delay(Duration.seconds(animIndex * 0.1))
                        .fadeIn(btn)
                        .buildAndPlay();

                animIndex++;
            });
        }

        private Node createBody() {
            double midY = getAppHeight() / 2.0;

            double distance = midY - 25;

            var btnContinue = createActionButton(localizedStringProperty("menu.continue"), this::fireContinue);
            var btn1 = createActionButton(localizedStringProperty("menu.newGame"), this::fireNewGame);
            var btn2 = createActionButton(createStringBinding(() -> "PLACEHOLDER 1"), this::fireNewGame);
            var btn3 = createActionButton(createStringBinding(() -> "PLACEHOLDER 2"), this::fireNewGame);
            var btn4 = createActionButton(createStringBinding(() -> "PLACEHOLDER 3"), this::fireNewGame);
            var btn5 = createActionButton(createStringBinding(() -> "PLACEHOLDER 4"), this::fireNewGame);
            var btn6 = createActionButton(createStringBinding(() -> "PLACEHOLDER 5"), this::fireNewGame);
            var btn7 = createActionButton(localizedStringProperty("menu.exit"), this::fireExit);

            Group group = new Group(btnContinue, btn1, btn2, btn3, btn4, btn5, btn6, btn7);

            double dtheta = Math.PI / (group.getChildren().size() - 1);
            double angle = Math.PI / 2;

            int i = 0;
            for (Node n : group.getChildren()) {

                Point2D vector = new Point2D(Math.cos(angle), -Math.sin(angle))
                        .normalize()
                        .multiply(distance)
                        .add(0, midY);

                n.setLayoutX(vector.getX() - (i == 0 || i == 7 ? 0 : 100));
                n.setLayoutY(vector.getY());

                angle -= dtheta;

                // slightly hacky way to get a nice looking radial menu
                // we assume that there are 8 items
                if (i == 0 || i == group.getChildren().size() - 2) {
                    angle -= dtheta / 2;
                } else if (i == 2 || i == 4) {
                    angle += dtheta / 4;
                } else if (i == 3) {
                    angle += dtheta / 2;
                }

                i++;
            }

            return group;
        }

        /**
         * Creates a new button with given name that performs given action on click/press.
         *
         * @param name  button name (with binding)
         * @param action button action
         * @return new button
         */
        private Node createActionButton(StringBinding name, Runnable action) {
            var bg = new Rectangle(200, 50);
            bg.setEffect(new BoxBlur());

            var text = getUIFactoryService().newText(name);
            text.setTranslateX(15);
            text.setFill(Color.BLACK);

            var btn = new StackPane(bg, text);

            bg.fillProperty().bind(when(btn.hoverProperty())
                    .then(Color.LIGHTGREEN)
                    .otherwise(Color.DARKGRAY)
            );

            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setOnMouseClicked(e -> action.run());

            // clipping
            buttons.add(btn);

            Rectangle clip = new Rectangle(200, 50);
            clip.translateXProperty().bind(btn.translateXProperty().negate());

            btn.setTranslateX(-200);
            btn.setClip(clip);
            btn.setCache(true);
            btn.setCacheHint(CacheHint.SPEED);

            return btn;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
