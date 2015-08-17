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
package com.almasb.fxgl.effect;

import com.almasb.fxgl.time.TimerManager;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Paint;

/**
 * Simple particle represented by a javafx.scene.shape.Circle.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
/*package-private*/ class Particle {

    /**
     * Top-left x
     */
    private double x;

    /**
     * Top-left y
     */
    private double y;

    private Point2D velocity;
    private double radius;

    private double alpha = 1.0;
    private double expireTime;
    private Paint color;
    private BlendMode blendMode;

    /*package-private*/ Particle(double x, double y, double radius, Point2D vel, double expireTime, Paint color, BlendMode blendMode) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.velocity = vel;
        this.expireTime = expireTime;
        this.color = color;
        this.blendMode = blendMode;
    }

    /*package-private*/ boolean update() {
        x += velocity.getX();
        y += velocity.getY();

        expireTime -= TimerManager.TIME_PER_FRAME;
        alpha -= 3 * TimerManager.toSeconds(TimerManager.TIME_PER_FRAME) * Math.random();
        if (expireTime <= 0 || alpha <= 0)
            return true;

        return false;
    }

    /*package-private*/ void render(GraphicsContext g, Point2D viewportOrigin) {
        g.setGlobalAlpha(alpha);
        g.setGlobalBlendMode(blendMode);
        g.setFill(color);
        g.fillOval(x - viewportOrigin.getX(), y - viewportOrigin.getY(), radius, radius);
    }
}
