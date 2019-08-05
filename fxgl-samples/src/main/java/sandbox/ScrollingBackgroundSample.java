/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.AccumulatedUpdateComponent;
import com.almasb.fxgl.dsl.components.OffscreenPauseComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.UserAction;
import dev.ScrollingBackgroundView;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScrollingBackgroundSample extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
    }

    @Override
    protected void initInput() {

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {

                player.translateX(10);
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                player.translateX(-10);
            }
        }, KeyCode.A);

        onKeyDown(KeyCode.F, "shake", () -> {
            getGameScene().getViewport().shakeTranslational(5);
        });
    }

    @Override
    protected void initGame() {
        getGameScene().getPaddingTop().setFill(Color.YELLOW);
        getGameScene().getPaddingBot().setFill(Color.RED);
        getGameScene().getPaddingRight().setFill(Color.GREEN);
        getGameScene().getPaddingLeft().setFill(Color.BLUE);



        //getGameScene().getRoot().setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        //getGameScene().getRoot().setStyle("-fx-background-color: black");


        player = entityBuilder()
                .buildAndAttach();

//        entityBuilder()
//                .at(410, 10)
//                .with(new OffscreenPauseComponent())
//                .with(new DebugComponent())
//                .viewWithBBox(new Rectangle(20, 20))
//                .buildAndAttach();

        getGameScene().getViewport().bindToEntity(player, 0, 0);
        getGameScene().getViewport().setLazy(true);

        entityBuilder()
                .view(new ScrollingBackgroundView(getAssetLoader().loadTexture("bg_wrap.png", 1066, 600),
                        Orientation.HORIZONTAL))
                .zIndex(-1)
                .buildAndAttach();

        // 1. load texture to be the background and specify orientation (horizontal or vertical)
        //getGameScene().addGameView();
    }

    // TODO: debug messages console dev pane
    static class DebugComponent extends AccumulatedUpdateComponent {
        public DebugComponent() {
            super(10);
        }

        @Override
        public void onAccumulatedUpdate(double tpfSum) {
            //System.out.println(tpfSum);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
