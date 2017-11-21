/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s06gameplay;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.gameplay.qte.QTE;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Shows how to use QTE (Quick Time Events).
 */
public class QTESample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("QTESample");
        settings.setVersion("0.1");





    }

    @Override
    protected void initGame() {

        // when app runs, every 5 seconds a QTE event will occur
        getMasterTimer().runAtInterval(() -> {

            // 1. get QTE access
            QTE qte = getGameplay().getQTE();

            // 2. start event with duration and keys to be pressed
            qte.start(yes -> {

                System.out.println("Successful? " + yes);

            }, Duration.seconds(3), KeyCode.F, KeyCode.X, KeyCode.G, KeyCode.L);

        }, Duration.seconds(5));
    }

    @Override
    protected void initUI() {
        Text text = getUIFactory().newText("Prepare! QTE runs every 5 seconds", Color.BLACK, 16.0);
        text.setTranslateY(50);

        getGameScene().addUINode(text);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
