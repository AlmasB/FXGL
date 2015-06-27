package com.almasb.fxgl.event;

import com.almasb.fxgl.GameApplication;

import javafx.animation.TranslateTransition;
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

    public void startQTE(double overallDuration, QTEHandler handler, KeyCode... keyCodes) {
        app.pause();
        app.addUINode(qteText);

        qteText.setTranslateY(app.getHeight() / 2);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), qteText);
        tt.setToY(50);
        tt.setOnFinished(event -> {
            currentQTE = new QTE(handler, () -> {
                app.removeUINode(currentQTE);
                currentQTE = null;

                app.resume();
            }, app.getWidth(), app.getHeight(), color, keyCodes);

            app.removeUINode(qteText);
            app.addUINode(currentQTE);

            app.runOnceAfter(() -> {
                if (currentQTE != null) {
                    app.removeUINode(currentQTE);

                    if (currentQTE.isActive()) {
                        handler.onFailure();
                    }

                    currentQTE = null;

                    app.resume();
                }
            }, overallDuration);
        });
        tt.play();
    }
}
