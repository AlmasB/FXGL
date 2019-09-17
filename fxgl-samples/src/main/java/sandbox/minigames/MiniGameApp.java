/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.minigames;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.minigames.MiniGameService;
import com.almasb.fxgl.minigames.lockpicking.LockPickView;
import com.almasb.fxgl.minigames.triggersequence.TriggerSequenceView;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MiniGameApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1066);
    }

    //            var view = new SweetSpotView();
//
//            FXGL.addUINode(view, 200, 200);


    //var spinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50));
    //view.getMinigame().getMinSuccessValue().bind(spinner.valueProperty());
    //FXGL.addUINode(spinner, 400, 100);

    int i = 1;

    private Text debugText;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "Hello", () -> {
        });

        onKeyDown(KeyCode.G, "Hello2", () -> {

            var manager = getMiniGameService();
            manager.startMiniGame(new LockPickView(), (result) -> {
                System.out.println(result.isSuccess() ? "SUCCESS" : "FAIL");
            });
        });

        onKeyDown(KeyCode.H, "Hello3", () -> {

        });
    }

    @Override
    protected void initGame() {
        debugText = getUIFactory().newText("", Color.BLACK, 36.0);

        getGameScene().setBackgroundRepeat("bg_10.png");

        addUINode(getUIFactory().newText("Mini-games dev area", Color.BLACK, 48.0), 100, 100);

        var btn = new FXGLButton("Trigger Sequence");
        btn.setOnAction(e -> {
            var manager = getMiniGameService();
//            manager.startMiniGame(new CircleTriggerMashView(), result -> {
//                debugText.setText(result.isSuccess() ? "Success" : "Fail");
//            });

            manager.startMiniGame(new TriggerSequenceView(), result -> {
                debugText.setText(result.isSuccess() ? "Success" : "Fail");
            });
        });

        addUINode(debugText, 600, 300);
        addUINode(btn, 150, 150);
        addUINode(new FXGLButton("Lockpicking"), 150, 200);
        addUINode(new FXGLButton("Trigger Mash"), 150, 400);

        var btnCheck = new FXGLButton("Skill Check");

        btnCheck.setOnAction(e -> {
            var manager = new MiniGameService();
            manager.startSweetSpot(10, (result) -> {

                debugText.setText(result.isSuccess() ? "Success" : "Fail");
                //addUINode(getUIFactory().newText(result.isSuccess() ? "SUCCESS" : "FAIL", Color.BLACK, 24.0), 20, i++ * 50);
            });
        });

        addUINode(btnCheck, 150, 250);

        //addUINode(new Rectangle(2, 2), getAppWidth() / 2 - 1, getAppHeight() / 2 - 1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
