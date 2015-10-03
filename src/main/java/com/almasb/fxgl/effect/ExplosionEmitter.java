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

public final class ExplosionEmitter extends ParticleEmitter {

    public ExplosionEmitter() {
        setNumParticles(100);
        setEmissionRate(0.0166);
    }

    @Override
    protected Particle emit(int i, double x, double y) {
        Point2D vel = new Point2D(Math.cos(i), Math.sin(i));

        Particle p = new Particle(new Point2D(x, y),
                vel.multiply(0.75),
                Point2D.ZERO,
                rand(5, 20),
                new Point2D(rand() * -0.1, rand() * -0.1),
                Duration.seconds(0.5),
                Color.rgb((int) rand(200, 255), 30, 20),
                i < getNumParticles() / 2 ? BlendMode.ADD : BlendMode.COLOR_BURN);
        return p;
    }
}
