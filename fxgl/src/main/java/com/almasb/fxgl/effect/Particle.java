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
package com.almasb.fxgl.effect;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Poolable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * Simple particle represented by a Shape or an Image.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Particle implements Poolable {

    /**
     * Top-left point in game world.
     */
    private Vec2 position = new Vec2();

    private Vec2 velocity = new Vec2();

    /**
     * Velocity acceleration.
     */
    private Vec2 gravity = new Vec2();

    /**
     * Radius in X, Y;
     */
    private Vec2 radius = new Vec2();

    /**
     * Radius change (acceleration).
     */
    private Vec2 scale = new Vec2();

    private double initialLife;

    /**
     * Current life.
     * When life <= 0, the particle dies.
     */
    private double life;

    /**
     * Color used when rendering at life == initialLife.
     */
    private Paint startColor;

    /**
     * Color used when rendering at life == 0.
     */
    private Paint endColor;

    /**
     * Blend mode used when rendering
     */
    private BlendMode blendMode;

    /**
     * Image from which the particle is created.
     * If the image is null, the particle is a software generated shape.
     */
    private Image image = null;

    private Consumer<Particle> control = null;

    public Particle(Point2D position, Point2D vel, Point2D gravity, double radius, Point2D scale, Duration expireTime, Paint startColor, Paint endColor, BlendMode blendMode) {
        this(null, position, vel, gravity, radius, scale, expireTime, startColor, endColor, blendMode);
    }

    public Particle(Image image, Point2D position, Point2D vel, Point2D gravity, double radius, Point2D scale, Duration expireTime, Paint startColor, Paint endColor, BlendMode blendMode) {
        init(image, position, vel, gravity, radius, scale, expireTime, startColor, endColor, blendMode);
    }

    public Particle() {
        // pooler ctor
        reset();
    }

    public final void init(Image image, Point2D position, Point2D vel, Point2D gravity, double radius, Point2D scale, Duration expireTime, Paint startColor, Paint endColor, BlendMode blendMode) {
        this.image = image;
        this.position.set(position);
        this.radius.set((float) radius, (float) radius);
        this.scale.set(scale);
        this.velocity.set(vel);
        this.gravity.set(gravity);
        this.startColor = startColor;
        this.endColor = endColor;
        this.blendMode = blendMode;
        this.initialLife = expireTime.toSeconds();
        this.life = initialLife;
    }

    @Override
    public void reset() {
        image = null;
        position.setZero();
        velocity.setZero();
        gravity.setZero();
        radius.setZero();
        scale.setZero();
        startColor = Color.TRANSPARENT;
        endColor = Color.TRANSPARENT;
        blendMode = BlendMode.SRC_OVER;
        initialLife = 0;
        life = 0;

        control = null;
    }

    /**
     * Set a direct controller to this particle.
     *
     * @param control particle control
     */
    public void setControl(Consumer<Particle> control) {
        this.control = control;
    }

    /**
     * @return true if particle died
     */
    boolean update(double tpf) {
        position.addLocal(velocity);
        velocity.addLocal(gravity);

        radius.addLocal(scale);

        life -= tpf;

        if (control != null)
            control.accept(this);

        return life <= 0 || radius.x <= 0 || radius.y <= 0;
    }

    /**
     * Renders particle to g context. Takes into
     * account the viewport origin, so if particle
     * XY is outside the viewport it will not be seen.
     *
     * @param g graphics context
     * @param viewportOrigin viewport origin
     */
    void render(GraphicsContext g, Point2D viewportOrigin) {
        double alpha = life / initialLife;

        g.setGlobalAlpha(alpha);
        g.setGlobalBlendMode(blendMode);

        Image particleImage = image != null ? image : ParticleEmitter.getCachedImage((Color) startColor, (Color) endColor, (int)(alpha * 100));

        g.save();

        g.translate(position.x - viewportOrigin.getX(), position.y - viewportOrigin.getY());
        g.scale(radius.x * 2 / particleImage.getWidth(), radius.y * 2 / particleImage.getHeight());
        g.drawImage(particleImage, 0, 0);

        g.restore();
    }
}
