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
package com.almasb.fxgl.ui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public final class ProgressBar extends Parent {

    private DoubleProperty minValue = new SimpleDoubleProperty(0.0);
    private DoubleProperty currentValue = new SimpleDoubleProperty(0.0);
    private DoubleProperty maxValue = new SimpleDoubleProperty(100.0);

    private DoubleProperty width = new SimpleDoubleProperty(200.0);
    private DoubleProperty height = new SimpleDoubleProperty(10.0);

    private Rectangle backgroundBar = new Rectangle();
    private Rectangle innerBar = new Rectangle();

    public ProgressBar() {
        innerBar.setTranslateX(5);
        innerBar.setTranslateY(3);
        innerBar.setFill(Color.GOLD);

        backgroundBar.widthProperty().bind(width);
        backgroundBar.heightProperty().bind(height);

        innerBar.widthProperty().bind(width.subtract(10).multiply(new DoubleBinding() {
            {
                super.bind(minValue, currentValue, maxValue);
            }

            @Override
            protected double computeValue() {
                return (currentValue.get() - minValue.get()) / (maxValue.get() - minValue.get());
            }

        }));
        innerBar.heightProperty().bind(height.subtract(6));

        backgroundBar.arcWidthProperty().bind(width.divide(8));
        backgroundBar.arcHeightProperty().bind(width.divide(8));
        innerBar.arcWidthProperty().bind(width.divide(8));
        innerBar.arcHeightProperty().bind(width.divide(8));

        DropShadow ds = new DropShadow(10, Color.WHITE);
        ds.setInput(new Glow(0.3));
        ds.setWidth(50);
        backgroundBar.setEffect(ds);

        ds = new DropShadow(10, Color.YELLOW);
        ds.setInput(new Glow(0.6));
        ds.setWidth(25);
        innerBar.setEffect(ds);

        getChildren().addAll(backgroundBar, innerBar);
    }

    public void setBackgroundFill(Paint color) {
        backgroundBar.setFill(color);
    }

    public void setFill(Paint color) {
        innerBar.setFill(color);
    }

    public void setWidth(double value) {
        width.set(value);
    }

    public void setHeight(double value) {
        height.set(value);
    }

    public void setMinValue(double value) {
        minValue.set(value);
    }

    public void setCurrentValue(double value) {
        currentValue.set(value);
    }

    public double getCurrentValue() {
        return currentValue.get();
    }

    public DoubleProperty currentValueProperty() {
        return currentValue;
    }

    public void setMaxValue(double value) {
        maxValue.set(value);
    }
}
