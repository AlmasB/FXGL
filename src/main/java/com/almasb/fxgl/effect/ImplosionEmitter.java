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

package com.almasb.fxgl.effect;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class ImplosionEmitter extends ParticleEmitter {

    public ImplosionEmitter() {
        setNumParticles(100);
        setEmissionRate(0.0166);
        setSize(5, 20);
        setColorFunction(() -> Color.rgb((int) rand(200, 255), 30, 20));
    }

    @Override
    protected Particle emit(int i, double x, double y) {
        Point2D vector = new Point2D(Math.cos(i), Math.sin(i));
        Point2D newPos = new Point2D(x, y).add(vector.multiply(25));

        return new Particle(newPos,
                newPos.subtract(new Point2D(x, y)).multiply(-0.05),
                Point2D.ZERO,
                getRandomSize(),
                new Point2D(rand() * -0.1, rand() * -0.1),
                Duration.seconds(0.5),
                getColorFunction().get(),
                i < getNumParticles() / 2 ? BlendMode.ADD : BlendMode.COLOR_DODGE);
    }
}
