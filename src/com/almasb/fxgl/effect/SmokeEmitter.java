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

import com.almasb.fxgl.time.TimerManager;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class SmokeEmitter extends ParticleEmitter {

    private Paint color = Color.rgb(230, 230, 230);

    public SmokeEmitter() {
        setNumParticles(5);
        setEmissionRate(1);
    }

    @Override
    protected Particle emit(int i, double x, double y) {
        Point2D spawn = new Point2D(i * (rand() - 0.5), (rand() - 1));
        Particle p = new Particle(new Point2D(x, y).add(rand(-1, 1), 0),
                new Point2D((rand() * 0.1) , rand() * -0.02 - 2.4),
                new Point2D(rand(0.00, 0.00), rand() * -0.03),
                rand(9, 10),
                new Point2D(-0.01, -0.05),
                TimerManager.toNanos(rand(6, 10)),
                color,
                BlendMode.ADD);

        p.setControl(particle -> {
            particle.setVelX(particle.getVelX() * 0.8);
            particle.setVelY(particle.getVelY() * 0.8);

            if (particle.getLife() < rand() - 0.2) {
                particle.setBlendMode(BlendMode.SRC_ATOP);
            }
        });
        return p;
    }
}
