/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.particle;

import com.almasb.fxgl.animation.AnimatedColor;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Poolable;
import com.almasb.fxgl.util.Consumer;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

/**
 * Simple particle represented by a Shape or an Image.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Particle implements Poolable {

    private Vec2 startPosition = new Vec2();

    /**
     * Current position.
     */
    private Vec2 position = new Vec2();

    /**
     * Pixels per second.
     */
    private Vec2 velocity = new Vec2();

    /**
     * Pixels per second^2.
     */
    private Vec2 acceleration = new Vec2();

    /**
     * Interpolator for current position given velocity and acceleration.
     */
    private Interpolator interpolator;

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

    private Ellipse view = new Ellipse();
    private ImageView imageView = new ImageView();
    private AnimatedColor colorAnimation = new AnimatedColor(Color.BLACK, Color.WHITE);

    public Node getView() {
        return imageView.getImage() != null ? imageView : view;
    }

    private Consumer<Particle> control = null;

    public Particle(Point2D position, Point2D vel, Point2D acceleration, double radius, Point2D scale, Duration expireTime, Paint startColor, Paint endColor, BlendMode blendMode) {
        this(null, position, vel, acceleration, radius, scale, expireTime, startColor, endColor, blendMode);
    }

    public Particle(Image image, Point2D position, Point2D vel, Point2D acceleration, double radius, Point2D scale, Duration expireTime, Paint startColor, Paint endColor, BlendMode blendMode) {
        init(image, position, vel, acceleration, radius, scale, expireTime, startColor, endColor, blendMode, Interpolator.LINEAR);
    }

    public Particle() {
        // pooler ctor
        reset();
    }

    public final void init(Image image, Point2D position, Point2D vel, Point2D acceleration, double radius, Point2D scale, Duration expireTime, Paint startColor, Paint endColor, BlendMode blendMode, Interpolator interpolator) {
        this.image = image;
        this.startPosition.set(position);
        this.position.set(position);
        this.radius.set((float) radius, (float) radius);
        this.scale.set(scale);
        this.velocity.set(vel);
        this.acceleration.set(acceleration);
        this.startColor = startColor;
        this.endColor = endColor;
        this.blendMode = blendMode;
        this.initialLife = expireTime.toSeconds();
        this.life = initialLife;
        this.interpolator = interpolator;

        imageView.setImage(image);
        colorAnimation = new AnimatedColor((Color)startColor, (Color)endColor, interpolator);
    }

    @Override
    public void reset() {
        image = null;
        startPosition.setZero();
        position.setZero();
        velocity.setZero();
        acceleration.setZero();
        radius.setZero();
        scale.setZero();
        startColor = Color.TRANSPARENT;
        endColor = Color.TRANSPARENT;
        blendMode = BlendMode.SRC_OVER;
        initialLife = 0;
        life = 0;
        interpolator = Interpolator.LINEAR;

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
        double progress = 1 - life / initialLife;

        // interpolate time based on progress
        double t = interpolator.interpolate(0, initialLife, progress);

        // s = s0 + v0*t + 0.5*a*t^2
        double x = startPosition.x + velocity.x * t + 0.5 * acceleration.x * t * t;
        double y = startPosition.y + velocity.y * t + 0.5 * acceleration.y * t * t;

        position.set((float) x, (float) y);

        radius.addLocal(scale);

        life -= tpf;

        if (control != null)
            control.accept(this);

        boolean dead = life <= 0 || radius.x <= 0 || radius.y <= 0;

        if (!dead) {
            double alpha = life / initialLife;

            if (image != null) {

                imageView.setScaleX(radius.x * 2 / image.getWidth());
                imageView.setScaleY(radius.y * 2 / image.getHeight());
                imageView.setLayoutX(x);
                imageView.setLayoutY(y);
                imageView.setOpacity(alpha);
                imageView.setBlendMode(blendMode);

            } else {

                view.setRadiusX(radius.x);
                view.setRadiusY(radius.y);
                view.setCenterX(radius.x);
                view.setCenterY(radius.y);
                view.setLayoutX(x);
                view.setLayoutY(y);
                view.setOpacity(alpha);
                view.setFill(colorAnimation.getValue(progress));
                view.setBlendMode(blendMode);
            }
        }

        return dead;
    }
}
