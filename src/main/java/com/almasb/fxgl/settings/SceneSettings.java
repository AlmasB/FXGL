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

public final class SceneSettings {
    private final double targetWidth;
    private final double targetHeight;
    private final double scaledWidth;
    private final double scaledHeight;
    private final double scaleRatio;
    private final String css;

    public SceneSettings(double width, double height,
            double scaledWidth, double scaledHeight,
            String css) {
        this.targetWidth = width;
        this.targetHeight = height;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        this.scaleRatio = scaledWidth / width;
        this.css = css;
    }

    public final double getTargetWidth() {
        return targetWidth;
    }

    public final double getTargetHeight() {
        return targetHeight;
    }

    public final double getScaledWidth() {
        return scaledWidth;
    }

    public final double getScaledHeight() {
        return scaledHeight;
    }

    public final double getScaleRatio() {
        return scaleRatio;
    }

    public final String getCSS() {
        return css;
    }
}
