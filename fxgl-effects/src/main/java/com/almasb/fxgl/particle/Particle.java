/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.particle;

import com.almasb.fxgl.animation.AnimatedColor;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Poolable;
import com.almasb.fxgl.core.util.Consumer;
import com.almasb.fxgl.core.util.Function;
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
@SuppressWarnings("PMD.UnusedPrivateField")
public class Particle implements Poolable {

    private Vec2 startPosition = new Vec2();

    /**
     * Current position.
     */
    public final Vec2 position = new Vec2();

    /**
     * Pixels per second.
     */
    public final Vec2 velocity = new Vec2();

    /**
     * Pixels per second^2.
     */
    public final Vec2 acceleration = new Vec2();

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
    public double life;

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
     * Allow view rotation based on velocity.
     */
    private boolean allowRotation;

    /**
     * Controls the particle position based on the equation.
     */
    private Function<Double, Point2D> equation;

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

    public Particle(
            Consumer<Particle> control,
            Point2D position,
            Point2D vel,
            Point2D acceleration,
            double radius,
            Point2D scale,
            Duration expireTime,
            Paint startColor,
            Paint endColor,
            BlendMode blendMode,
            boolean allowRotation,
            Function<Double, Point2D> equation) {

        this(control, null, position, vel, acceleration, radius, scale, expireTime, startColor, endColor, blendMode, allowRotation, equation);
    }

    public Particle(
            Consumer<Particle> control,
            Image image,
            Point2D position,
            Point2D vel,
            Point2D acceleration,
            double radius,
            Point2D scale,
            Duration expireTime,
            Paint startColor,
            Paint endColor,
            BlendMode blendMode,
            boolean allowRotation,
            Function<Double, Point2D> equation) {

        init(control, image, position, vel, acceleration, radius, scale, expireTime, startColor, endColor, blendMode, Interpolator.LINEAR, allowRotation, equation);
    }

    public Particle() {
        // pooler ctor
        reset();
    }

    public final void init(
            Consumer<Particle> control,
            Image image,
            Point2D position,
            Point2D vel,
            Point2D acceleration,
            double radius,
            Point2D scale,
            Duration expireTime,
            Paint startColor,
            Paint endColor,
            BlendMode blendMode,
            Interpolator interpolator,
            boolean allowRotation,
            Function<Double, Point2D> equation) {

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
        this.allowRotation = allowRotation;
        this.equation = equation;
        this.control = control;

        imageView.setImage(image);
        colorAnimation = new AnimatedColor((Color)startColor, (Color)endColor);
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
        allowRotation = false;
        equation = null;

        control = null;
    }

    private Vec2 moveVector = new Vec2();

    /**
     * @return true if particle died
     */
    boolean update(double tpf) {
        double progress = 1 - life / initialLife;

        // interpolate time based on progress
        double t = interpolator.interpolate(0, initialLife, progress);

        double x;
        double y;

        if (control != null) {
            control.accept(this);

            velocity.addLocal(acceleration);
            x = position.x + velocity.x;
            y = position.y + velocity.y;

        } else {
            if (equation == null) {
                // s = s0 + v0*t + 0.5*a*t^2
                x = startPosition.x + velocity.x * t + 0.5 * acceleration.x * t * t;
                y = startPosition.y + velocity.y * t + 0.5 * acceleration.y * t * t;
            } else {
                Point2D newPos = equation.apply(t);
                x = startPosition.x + newPos.getX();
                y = startPosition.y + newPos.getY();
            }
        }

        moveVector.set((float)x - position.x, (float)y - position.y);

        position.set((float) x, (float) y);

        radius.addLocal(scale);

        life -= tpf;

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

                if (allowRotation) {
                    imageView.setRotate(moveVector.angle());
                }

            } else {

                view.setRadiusX(radius.x);
                view.setRadiusY(radius.y);
                view.setCenterX(radius.x);
                view.setCenterY(radius.y);
                view.setLayoutX(x);
                view.setLayoutY(y);
                view.setOpacity(alpha);
                view.setFill(colorAnimation.getValue(progress, interpolator));
                view.setBlendMode(blendMode);

                if (allowRotation) {
                    view.setRotate(moveVector.angle());
                }
            }
        }

        return dead;
    }
}
