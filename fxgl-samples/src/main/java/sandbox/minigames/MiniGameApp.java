/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.minigames;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.minigames.lockpicking.LockPickView;
import com.almasb.fxgl.minigames.sweetspot.SweetSpotView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import minigames.MiniGameManager;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MiniGameApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
    }

    //            var view = new SweetSpotView();
//
//            FXGL.addUINode(view, 200, 200);


    //var spinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50));
    //view.getMinigame().getMinSuccessValue().bind(spinner.valueProperty());
    //FXGL.addUINode(spinner, 400, 100);

    int i = 1;

    @Override
    protected void initInput() {
        FXGL.onKeyDown(KeyCode.F, "Hello", () -> {

            var manager = new MiniGameManager();
            manager.startSweetSpot(10, (result) -> {

                FXGL.addUINode(FXGL.getUIFactory().newText(result.isSuccess() ? "SUCCESS" : "FAIL", Color.BLACK, 24.0), 20, i++ * 50);
            });
        });

        FXGL.onKeyDown(KeyCode.G, "Hello2", () -> {

            var manager = new MiniGameManager();
            manager.startMiniGame(new LockPickView(), (result) -> {
                System.out.println(result.isSuccess() ? "SUCCESS" : "FAIL");
            });
        });
    }

    @Override
    protected void initGame() {
        FXGL.getGameScene().setBackgroundRepeat("bg_10.png");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
