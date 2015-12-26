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

package com.almasb.fxgl.gameplay;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.event.AchievementEvent;
import com.almasb.fxgl.event.NotificationEvent;
import com.almasb.fxgl.scene.GameScene;
import com.almasb.fxgl.ui.Position;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * Allows to easily push notifications.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class FXGLNotificationService implements NotificationService {

    /**
     * The logger
     */
    private static final Logger log = FXGLLogger.getLogger("FXGL.NotificationService");

    private Queue<NotificationView> queue = new ArrayDeque<>();

    private Position position = Position.TOP;

    /**
     *
     * @return notification position
     */
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * Set position of future notifications.
     *
     * @param position where to show notification
     */
    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    private Color backgroundColor = Color.BLACK;

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set background color of notifications.
     *
     * @param backgroundColor the color
     */
    @Override
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private boolean showing = false;

    private Pane parent;

    @Inject
    private FXGLNotificationService(GameScene gameScene) {
        this.parent = gameScene.getRoot();

        GameApplication.getService(ServiceType.EVENT_BUS).addEventHandler(AchievementEvent.ANY, event -> {
            pushNotification("You got an achievement! " + event.getAchievement().getName());
        });

        log.finer("Service [NotificationService] initialized");
    }

    private void popNotification(NotificationView notificationView) {
        boolean removed = parent.getChildren().remove(notificationView);
        if (!removed) {
            return;
        }

        if (!queue.isEmpty()) {
            showNotification(queue.poll());
        }
        else {
            showing = false;
        }
    }

    /**
     * Shows a notification with given text.
     * Only 1 notification can be shown at a time.
     * If a notification is being shown already, next notifications
     * will be queued to be shown as soon as space available.
     *
     * @param text the text to show
     */
    @Override
    public void pushNotification(String text) {
        NotificationView notificationView = createNotificationView(text);

        if (showing)
            queue.add(notificationView);
        else
            showNotification(notificationView);
    }

    private void showNotification(NotificationView notificationView) {
        showing = true;
        parent.getChildren().add(notificationView);
        notificationView.show();

        GameApplication.getService(ServiceType.EVENT_BUS)
                .fireEvent(new NotificationEvent(notificationView.getNotification()));
    }

    private NotificationView createNotificationView(String text) {
        ScaleTransition in = new ScaleTransition(Duration.seconds(0.3));
        in.setFromX(0);
        in.setFromY(0);
        in.setToX(1);
        in.setToY(1);

        ScaleTransition out = new ScaleTransition(Duration.seconds(0.3));
        out.setFromX(1);
        out.setFromY(1);
        out.setToX(0);
        out.setToY(0);

        NotificationView notificationView = new NotificationView(new Notification(text), backgroundColor, in, out);
        notificationView.setScaleX(0);
        notificationView.setScaleY(0);

        double x = 0, y = 0;

        switch (position) {
            case LEFT:
                x = 50;
                y = parent.getHeight() / 2 - (UIFactory.heightOf(text, 12) + 10) / 2;
                break;
            case RIGHT:
                x = parent.getWidth() - (UIFactory.widthOf(text, 12) + 20) - 50;
                y = parent.getHeight() / 2 - (UIFactory.heightOf(text, 12) + 10) / 2;
                break;
            case TOP:
                x = parent.getWidth() / 2 - (UIFactory.widthOf(text, 12) + 20) / 2;
                y = 50;
                break;
            case BOTTOM:
                x = parent.getWidth() / 2 - (UIFactory.widthOf(text, 12) + 20) / 2;
                y = parent.getHeight() - (UIFactory.heightOf(text, 12) + 10) - 50;
                break;
        }

        notificationView.setTranslateX(x);
        notificationView.setTranslateY(y);

        in.setNode(notificationView);
        out.setNode(notificationView);
        out.setOnFinished(e -> popNotification(notificationView));

        return notificationView;
    }
}
