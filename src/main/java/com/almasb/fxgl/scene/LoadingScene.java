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

package com.almasb.fxgl.scene;

import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.google.inject.Inject;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class LoadingScene extends FXGLScene {

    private ProgressIndicator progress;
    private Text text;

    @Inject
    private LoadingScene(ReadOnlyGameSettings settings) {
        Rectangle bg = new Rectangle(settings.getWidth(), settings.getHeight(), Color.rgb(0, 0, 10));

        progress = new ProgressIndicator();
        progress.setPrefSize(200, 200);
        progress.setTranslateX(settings.getWidth() / 2 - 100);
        progress.setTranslateY(settings.getHeight() / 2 - 100);

        text = new Text();
        text.setFill(Color.WHITE);
        text.setFont(Font.font(24));
        text.setTranslateX(settings.getWidth() / 2 - 100);
        text.setTranslateY(settings.getHeight() * 4 / 5);

        getRoot().getChildren().addAll(bg, progress, text);
    }

    public void bind(Task<?> task) {
        progress.progressProperty().bind(task.progressProperty());
        text.textProperty().bind(task.messageProperty());
    }
}
