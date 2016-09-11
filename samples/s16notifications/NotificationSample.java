/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package s16notifications;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.gameplay.FXGLNotificationService;
import com.almasb.fxgl.gameplay.NotificationService;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
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
        settings.setShowFPS(true);

        // 2. by default SlidingNotificationService is used
        // but you can provide your own or use another built-in provider, e.g. FXGLNotificationService
        settings.addServiceType(new ServiceType<NotificationService>() {

            @Override
            public Class<NotificationService> service() {
                return NotificationService.class;
            }

            @Override
            public Class<? extends NotificationService> serviceProvider() {
                return FXGLNotificationService.class;
            }
        });

        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addInputMapping(new InputMapping("Show Notification", KeyCode.F));
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {}

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    @OnUserAction(name = "Show Notification", type = ActionType.ON_ACTION_BEGIN)
    public void showNotification() {
        // 1. get notification service and push a message
        getNotificationService().pushNotification("Some Message! Tick: " + getTick());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
