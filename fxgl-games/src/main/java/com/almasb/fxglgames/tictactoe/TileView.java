/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.app.FXGL;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileView extends StackPane {

    private TicTacToeApp app;

    private Arc arc = new Arc(34, 37, 34, 37, 0, 0);
    private Line line1 = new Line(0, 0, 0, 0);
    private Line line2 = new Line(75, 0, 75, 0);

    public TileView(TileEntity tile) {
        app = FXGL.getAppCast();

        Rectangle bg = new Rectangle(app.getWidth() / 3, app.getHeight() / 3, Color.rgb(13, 222, 236));

        Rectangle bg2 = new Rectangle(app.getWidth() / 4, app.getHeight() / 4, Color.rgb(250, 250, 250, 0.25));
        bg2.setArcWidth(25);
        bg2.setArcHeight(25);

        arc.setFill(null);
        arc.setStroke(Color.BLACK);
        arc.setStrokeWidth(3);

        line1.setStrokeWidth(3);
        line2.setStrokeWidth(3);

        line1.setVisible(false);
        line2.setVisible(false);

        getChildren().addAll(bg, bg2, arc, line1, line2);

        tile.getComponentUnsafe(TileValueComponent.class).valueProperty().addListener((observable, oldValue, newValue) -> {
            animate(newValue);
        });

        setOnMouseClicked(e -> app.onUserMove(tile));
    }

    public void animate(TileValue value) {
        if (value == TileValue.O) {
            KeyFrame frame = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(arc.lengthProperty(), 360));

            Timeline timeline = new Timeline(frame);
            timeline.play();
        } else {

            line1.setVisible(true);
            line2.setVisible(true);

            KeyFrame frame1 = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(line1.endXProperty(), 75),
                    new KeyValue(line1.endYProperty(), 75));

            KeyFrame frame2 = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(line2.endXProperty(), 0),
                    new KeyValue(line2.endYProperty(), 75));

            Timeline timeline = new Timeline(frame1, frame2);
            timeline.play();
        }
    }
}
