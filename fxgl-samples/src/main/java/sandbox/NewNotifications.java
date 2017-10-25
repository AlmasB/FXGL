/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.annotation.OnUserAction;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * Shows how to use notifications.
 * When app is running press F to show a notification.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class NewNotifications extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("NewNotifications");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addInputMapping(new InputMapping("Show Notification", KeyCode.F));
    }

    @Override
    protected void initGame() {
        getNotificationService().setBackgroundColor(Color.BLUE);
    }

    @OnUserAction(name = "Show Notification", type = ActionType.ON_ACTION_BEGIN)
    public void showNotification() {
        // 1. get notification service and push a message
        getNotificationService().pushNotification("Some Message! Tick: " + getTick());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
