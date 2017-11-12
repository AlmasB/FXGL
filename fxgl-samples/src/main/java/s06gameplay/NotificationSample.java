/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay;

import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 * Shows how to use notifications.
 * When app is running press F to show a notification.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class NotificationSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("NotificationSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);

        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addInputMapping(new InputMapping("Show Notification", KeyCode.F));
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
