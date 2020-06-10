/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.notification.NotificationService;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to provide custom services.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class CustomServicesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setEngineServiceProvider(NotificationService.class, MyNotificationServiceProvider.class);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "Notify", () -> getNotificationService().pushNotification("Hello! " + random(1, 10000)));
    }

    public static class MyNotificationServiceProvider extends NotificationService {
        @Override
        public void pushNotification(String message) {
            System.out.println("Notify: " + message);
        }

        @Override
        public void setTextColor(Color textColor) {

        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public void setBackgroundColor(Color backgroundColor) {

        }

        @Override
        public Color getBackgroundColor() {
            return Color.BLACK;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
