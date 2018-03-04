/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LagApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(20 * 70);
        settings.setHeight(10 * 70);
        settings.setProfilingEnabled(true);
        //settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                getGameScene().getViewport().setX(getGameScene().getViewport().getX() - 5);
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                getGameScene().getViewport().setX(getGameScene().getViewport().getX() + 5);
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                getGameScene().getViewport().setY(getGameScene().getViewport().getY() - 5);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                getGameScene().getViewport().setY(getGameScene().getViewport().getY() + 5);
            }
        }, KeyCode.S);
    }

    @Override
    protected void initGame() {
        getGameWorld().setLevelFromMap("lag1.tmx");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
