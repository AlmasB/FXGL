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

import java.util.function.Consumer;

import com.almasb.fxgl.time.TimerManager;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

/**
 * Simple particle represented by a circle.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Particle {

    /**
     * Top-left x in game world
     */
    private double x;

    /**
     * Top-left y in game world
     */
    private double y;

    /**
     * Velocity x
     */
    private double velX;

    /**
     * Velocity y
     */
    private double velY;

    /**
     * Velocity acceleration
     */
    private Point2D gravity;

    /**
     * Radius in X
     */
    private double radiusX;

    /**
     * Radius in Y
     */
    private double radiusY;

    /**
     * Radius change (acceleration)
     */
    private Point2D scale;

    /**
     * Percentage ratio of particle lifetime.
     * 1.0 = 100% (just spawned).
     * 0.5 =  50% (has spent 50% of its time).
     * 0.0 =   0% (dead).
     */
    private double life = 1.0;

    /**
     * Percentage ratio by which life is reduced
     * every frame.
     */
    private double decay;

    /**
     * Color used when rendering
     */
    private Paint color;

    /**
     * Blend mode used when rendering
     */
    private BlendMode blendMode;

    private Consumer<Particle> control;

    public Particle(Point2D position, Point2D vel, Point2D gravity, double radius, Point2D scale, Duration expireTime, Paint color, BlendMode blendMode) {
        this.x = position.getX();
        this.y = position.getY();
        this.radiusX = radius;
        this.radiusY = radius;
        this.scale = scale;
        this.velX = vel.getX();
        this.velY = vel.getY();
        this.gravity = gravity;
        this.color = color;
        this.blendMode = blendMode;
        this.decay = TimerManager.tpfSeconds() / expireTime.toSeconds();
    }

    /**
     * Set a direct controller to this particle.
     *
     * @param control
     */
    public void setControl(Consumer<Particle> control) {
        this.control = control;
    }

    /**
     * @return true if particle died
     */
    boolean update() {
        x += velX;
        y += velY;

        velX += gravity.getX();
        velY += gravity.getY();

        radiusX += scale.getX();
        radiusY += scale.getY();

        life -= decay;

        if (control != null)
            control.accept(this);

        if (life <= 0 || radiusX <= 0 || radiusY <= 0)
            return true;

        return false;
    }

    /**
     * Renders particle to g context. Takes into
     * account the viewport origin, so if particle
     * XY is outside the viewport it will not be seen.
     *
     * @param g
     * @param viewportOrigin
     */
    void render(GraphicsContext g, Point2D viewportOrigin) {
        g.setGlobalAlpha(life);
        g.setGlobalBlendMode(blendMode);
        g.setFill(color);
        g.fillOval(x - viewportOrigin.getX(), y - viewportOrigin.getY(), radiusX, radiusY);
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public Point2D getGravity() {
        return gravity;
    }

    public void setGravity(Point2D gravity) {
        this.gravity = gravity;
    }

    public double getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(double radiusX) {
        this.radiusX = radiusX;
    }

    public double getRadiusY() {
        return radiusY;
    }

    public void setRadiusY(double radiusY) {
        this.radiusY = radiusY;
    }

    public Point2D getScale() {
        return scale;
    }

    public void setScale(Point2D scale) {
        this.scale = scale;
    }

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }

    public Paint getColor() {
        return color;
    }

    public void setColor(Paint color) {
        this.color = color;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
    }

    public double getLife() {
        return life;
    }
}
