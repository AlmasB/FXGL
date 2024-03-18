/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.minigames;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.minigames.sweetspot.SweetSpotMiniGame;
import com.almasb.fxgl.minigames.sweetspot.SweetSpotView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.input.KeyCode.*;

/**
 * Shows how to use MiniGameService.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MiniGameSample extends GameApplication {

    private Text debugText;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        debugText = getUIFactoryService().newText("Result", Color.BLACK, 36.0);
        addUINode(debugText, 50, 50);

        var btn1 = new Button("Trigger Sequence");
        btn1.setOnAction(e -> {
            getMiniGameService().startTriggerSequence(List.of(S, F, A), 1.0, result -> {
                debugText.setText(result.isSuccess() ? "Success" : "Fail");
            });
        });

        var btn2 = new Button("Trigger Mash");
        btn2.setOnAction(e -> {
            getMiniGameService().startTriggerMash(new KeyTrigger(C), result -> {
                debugText.setText(result.isSuccess() ? "Success" : "Fail");
            });
        });

        var btn3 = new Button("Skill Check");
        btn3.setOnAction(e -> {
            getMiniGameService().startSweetSpot(20, result -> {
                debugText.setText(result.isSuccess() ? "Success" : "Fail");
            });
        });

        var btn4 = new Button("Circuit Breaker");
        btn4.setOnAction(e -> {
            getMiniGameService().startCircuitBreaker(15, 10, 2, 18, Duration.seconds(0.25), result -> {
                debugText.setText(result.isSuccess() ? "Success" : "Fail");
            });
        });

        var btn5 = new Button("Custom Mini Game");
        btn5.setOnAction(e -> {
            var game = new SweetSpotMiniGame();
            game.randomizeRange(35);
            getMiniGameService().startMiniGameInGame(new SweetSpotView(game), result -> {
                debugText.setText(result.isSuccess() ? "Success" : "Fail");
            });
        });

        addUINode(new VBox(
                btn1, btn2, btn3, btn4, btn5
        ), 50, 70);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
