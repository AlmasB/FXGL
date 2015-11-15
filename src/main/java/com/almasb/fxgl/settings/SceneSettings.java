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
package com.almasb.fxgl.settings;

import com.almasb.fxgl.util.FXGLLogger;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.util.logging.Logger;

public final class SceneSettings {
    /**
     * The logger
     */
    private static final Logger log = FXGLLogger.getLogger("FXGL.SceneSettings");

    private final DoubleProperty targetWidth;
    private final DoubleProperty targetHeight;
    private final DoubleProperty scaledWidth;
    private final DoubleProperty scaledHeight;
    private final DoubleProperty scaleRatio;
    private final String css;

    private double appW, appH;

    /**
     * Screen bounds
     */
    private Rectangle2D bounds;

    public SceneSettings(double width, double height,
                         Rectangle2D bounds,
                         String css) {
        appW = width;
        appH = height;
        this.targetWidth = new SimpleDoubleProperty(width);
        this.targetHeight = new SimpleDoubleProperty(height);
        this.css = css;
        this.bounds = bounds;
        this.scaledWidth = new SimpleDoubleProperty();
        this.scaledHeight = new SimpleDoubleProperty();
        this.scaleRatio = new SimpleDoubleProperty();

        computeScaledSize();
    }

    private void computeScaledSize() {
        double newW = getTargetWidth();
        double newH = getTargetHeight();

        if (newW > bounds.getWidth() || newH > bounds.getHeight()) {
            log.finer("App size > screen size");

            double ratio = newW / newH;

            for (int newWidth = (int) bounds.getWidth(); newWidth > 0; newWidth--) {
                if (newWidth / ratio <= bounds.getHeight()) {
                    newW = newWidth;
                    newH = newWidth / ratio;
                    break;
                }
            }
        }

        scaledWidth.set(newW);
        scaledHeight.set(newH);
        scaleRatio.set(newW / appW);

        log.finer("Target size: " + getTargetWidth() + "x" + getTargetHeight() + "@" + 1.0);
        log.finer("New size:    " + newW  + "x" + newH   + "@" + getScaleRatio());
    }

    public void setNewTargetSize(double w, double h) {
        targetWidth.set(w);
        targetHeight.set(h);
        computeScaledSize();
    }

    public DoubleProperty targetWidthProperty() {
        return targetWidth;
    }

    public DoubleProperty targetHeightProperty() {
        return targetHeight;
    }

    public DoubleProperty scaledWidthProperty() {
        return scaledWidth;
    }

    public DoubleProperty scaledHeightProperty() {
        return scaledHeight;
    }

    public DoubleProperty scaleRatioProperty() {
        return scaleRatio;
    }

    public final double getTargetWidth() {
        return targetWidth.get();
    }

    public final double getTargetHeight() {
        return targetHeight.get();
    }

    public final double getScaledWidth() {
        return scaledWidth.get();
    }

    public final double getScaledHeight() {
        return scaledHeight.get();
    }

    public final double getScaleRatio() {
        return scaleRatio.get();
    }

    public final String getCSS() {
        return css;
    }

    public static final class SceneDimension extends Dimension2D {

        /**
         * Constructs a <code>SceneDimension</code> with the specified width and
         * height.
         *
         * @param width  the width
         * @param height the height
         */
        public SceneDimension(@NamedArg("width") double width, @NamedArg("height") double height) {
            super(width, height);
        }

        @Override
        public String toString() {
            return String.format("%.0fx%.0f", getWidth(), getHeight());
        }
    }
}
