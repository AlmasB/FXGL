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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.event;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.almasb.fxgl.GameApplication;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class QTEManager {

    private GameApplication app;

    private Color color = Color.BLACK;
    private Text qteText = new Text("Prepare for QTE! Release ALL keys!");

    private QTE currentQTE = null;

    public QTEManager(GameApplication app) {
        this.app = app;
    }

    public void init() {
        qteText.setFont(Font.font(24));
        qteText.setTranslateX(app.getWidth() / 2 - qteText.getLayoutBounds().getWidth() / 2);
        qteText.setTranslateY(app.getHeight() / 2);
    }

    public void setColor(Color color) {
        this.color = color;
        qteText.setFill(color);
    }

    /**
     * Called on Key Released Event. This is a JavaFX Event Handler
     *
     * @param event
     */
    public void keyReleasedHandler(KeyEvent event) {
        if (currentQTE == null)
            return;

        currentQTE.pressed(event.getCode());
    }

    /**
     * Start a Quick Time Event
     *
     * @param overallDuration in nanoseconds for the whole event, i.e. for all keys
     * @param handler the handler for the event
     * @param keyCodes keys that need to pressed during QTE, order determines how
     *          they appear on the screen
     */
    public void startQTE(double overallDuration, QTEHandler handler, KeyCode key, KeyCode... keyCodes) {
        app.pause();
        app.addUINodes(qteText);

        qteText.setTranslateY(app.getHeight() / 2);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), qteText);
        tt.setToY(50);
        tt.setOnFinished(event -> {
            currentQTE = new QTE(handler, () -> {
                app.removeUINode(currentQTE);
                currentQTE = null;

                app.resume();
            }, app.getWidth(), app.getHeight(), color, key, keyCodes);

            app.removeUINode(qteText);
            app.addUINodes(currentQTE);

            ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
            thread.schedule(() -> {
                Platform.runLater(() -> {
                    if (currentQTE != null) {
                        app.removeUINode(currentQTE);

                        if (currentQTE.isActive()) {
                            handler.onFailure();
                        }

                        currentQTE = null;

                        app.resume();
                    }
                });

                thread.shutdown();
            }, (long)overallDuration, TimeUnit.NANOSECONDS);
        });
        tt.play();
    }
}
