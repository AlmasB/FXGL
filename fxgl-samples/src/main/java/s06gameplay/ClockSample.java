/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.gameplay.rpg.InGameClock;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use an in-game clock.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ClockSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ClockSample");
        settings.setVersion("0.1");




    }

    private InGameClock clock;

    @Override
    protected void initGame() {
        clock = getGameplay().getClock();
        clock.start();

        Rectangle rect = new Rectangle(50, 50);
        rect.fillProperty().bind(
                Bindings.when(clock.dayProperty()).then(Color.YELLOW).otherwise(Color.RED)
        );

        getGameScene().addUINode(rect);

        clock.runAt(() -> {
            System.out.println("It's 02:30");
        }, 2, 30);

        clock.runAtHour(() -> {
            System.out.println("It's 06:00");
        }, 6);

        clock.gameHourProperty().addListener((obs, o, newValue) -> {
            System.out.println(newValue);
        });
    }

    @Override
    protected void initUI() {
        getUIFactory().centerText(clock.textView());

        getGameScene().addUINode(clock.textView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
