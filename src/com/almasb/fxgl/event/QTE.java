package com.almasb.fxgl.event;

import java.util.ArrayDeque;
import java.util.Queue;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov
 * @version 1.0
 *
 */
public final class QTE extends HBox {

    private static final Font FONT = Font.font(28);

    private QTEHandler handler;
    private Runnable onFinished;
    private Queue<KeyCode> queue = new ArrayDeque<>();

    private Text text = new Text();

    public QTE(QTEHandler handler, Runnable onFinishedScript, double appW, double appH, KeyCode... keys) {
        super(15);

        if (keys == null || keys.length == 0)
            throw new IllegalArgumentException("QTE keys must not be null or empty");

        this.handler = handler;
        this.onFinished = onFinishedScript;
        for (KeyCode key : keys)
            queue.offer(key);

        Line left = new Line(0, 0, appW / 2 - 20, 0);
        Line right = new Line(0, 0, appW / 2 - 20, 0);

        text.setText(queue.peek().toString());
        text.setFont(FONT);

        setAlignment(Pos.CENTER);
        getChildren().addAll(left, text, right);

        setTranslateY(appH / 2);
    }

    public boolean isActive() {
        return !queue.isEmpty();
    }

    public void pressed(KeyCode key) {
        if (!isActive())
            return;

        KeyCode k = queue.poll();

        if (k != key) {
            queue.clear();
            handler.onFailure();
            onFinished.run();
        }
        else {
            if (isActive()) {
                text.setText(queue.peek().toString());
            }
            else {
                handler.onSuccess();
                onFinished.run();
            }
        }
    }
}
