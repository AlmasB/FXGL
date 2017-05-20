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

package com.almasb.fxgl.physics.box2d.particle;

import javafx.scene.paint.Color;

/**
 * Small color object for each particle
 *
 * @author dmurph
 */
public class ParticleColor {
    public byte r, g, b, a;

    public ParticleColor() {
        r = (byte) 127;
        g = (byte) 127;
        b = (byte) 127;
        a = (byte) 50;
    }

    public ParticleColor(byte r, byte g, byte b, byte a) {
        set(r, g, b, a);
    }

    public ParticleColor(Color color) {
        set(color);
    }

    public void set(Color color) {
        r = (byte) (255 * color.getRed());
        g = (byte) (255 * color.getGreen());
        b = (byte) (255 * color.getBlue());
        a = (byte) 255;
    }

    public void set(ParticleColor color) {
        r = color.r;
        g = color.g;
        b = color.b;
        a = color.a;
    }

    public boolean isZero() {
        return r == 0 && g == 0 && b == 0 && a == 0;
    }

    public void set(byte r, byte g, byte b, byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
