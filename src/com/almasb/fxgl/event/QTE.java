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

import java.util.ArrayDeque;
import java.util.Queue;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov
 * @version 1.0
 *
 */
/*package-private*/ final class QTE extends HBox {

    private static final Font FONT = Font.font(28);

    private QTEHandler handler;
    private Runnable onFinished;
    private Queue<KeyCode> queue = new ArrayDeque<>();

    private Text text = new Text();

    /*package-private*/ QTE(QTEHandler handler, Runnable onFinishedScript, double appW, double appH, Color color, KeyCode key, KeyCode... keys) {
        super(15);

        this.handler = handler;
        this.onFinished = onFinishedScript;

        queue.offer(key);
        for (KeyCode k : keys)
            queue.offer(k);

        Line left = new Line(0, 0, appW / 2 - 20, 0);
        Line right = new Line(0, 0, appW / 2 - 20, 0);
        left.setStroke(color);
        right.setStroke(color);

        text.setText(queue.peek().toString());
        text.setFont(FONT);
        text.setFill(color);

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
