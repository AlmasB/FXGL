/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.util.TriFunction;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A general particle emitter.
 * The configuration is done via setters, which allow
 * changing how the particle is emitted and rendered.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ParticleEmitter {

    private static final List<Particle> EMPTY = Collections.emptyList();

    private Random random = new Random();
    private int numParticles = 25;
    private double emissionRate = 1.0;

    private double sizeMin = 9;
    private double sizeMax = 12;

    /**
     * @return minimum particle size
     */
    public final double getSizeMin() {
        return sizeMin;
    }

    /**
     * @return maximum particle size
     */
    public final double getSizeMax() {
        return sizeMax;
    }

    /**
     * @return random size between min and max size
     */
    protected final double getRandomSize() {
        return rand(getSizeMin(), getSizeMax());
    }

    /**
     * Set size to particles.
     * The created size will be a random value
     * between min (incl) and max (excl).
     *
     * @param min minimum size
     * @param max maximum size
     */
    public final void setSize(double min, double max) {
        sizeMin = min;
        sizeMax = max;
    }

    private Supplier<Paint> colorFunction = () -> Color.TRANSPARENT;

    /**
     * @return particles color function
     */
    public final Supplier<Paint> getColorFunction() {
        return colorFunction;
    }

    /**
     * Set color function to particles created by this emitter.
     * The supplier function will be invoked every time a new
     * particle is emitted.
     *
     * @param colorFunction particles color function.
     */
    public final void setColorFunction(Supplier<Paint> colorFunction) {
        this.colorFunction = colorFunction;
    }

    private Supplier<Point2D> gravityFunction = () -> Point2D.ZERO;

    /**
     *
     * @return gravity function
     */
    public final Supplier<Point2D> getGravityFunction() {
        return gravityFunction;
    }

    /**
     * Set gravity function. The supplier function is invoked
     * every time when a new particle is spawned.
     *
     * Adds gravitational pull to particles. Once created
     * the particle's movement will be biased towards
     * the vector.
     *
     * @param gravityFunction gravity vector supplier function
     * @defaultValue (0, 0)
     */
    public final void setGravityFunction(Supplier<Point2D> gravityFunction) {
        this.gravityFunction = gravityFunction;
    }

    private TriFunction<Integer, Double, Double, Point2D> velocityFunction = (i, x, y) -> Point2D.ZERO;

    /**
     * Set initial velocity function. Particles when spawned will use the function
     * to obtain initial velocity.
     *
     * @param velocityFunction the velocity function
     */
    public final void setVelocityFunction(TriFunction<Integer, Double, Double, Point2D> velocityFunction) {
        this.velocityFunction = velocityFunction;
    }

    private TriFunction<Integer, Double, Double, Point2D> spawnPointFunction = (i, x, y) -> Point2D.ZERO;

    /**
     * Particles will use the function to obtain spawn points.
     *
     * @param spawnPointFunction supplier of spawn points
     */
    public final void setSpawnPointFunction(TriFunction<Integer, Double, Double, Point2D> spawnPointFunction) {
        this.spawnPointFunction = spawnPointFunction;
    }

    private TriFunction<Integer, Double, Double, Point2D> scaleFunction = (i, x, y) -> Point2D.ZERO;

    /**
     * Scale function defines how the size of particles change over time.
     *
     * @param scaleFunction scaling function
     */
    public final void setScaleFunction(TriFunction<Integer, Double, Double, Point2D> scaleFunction) {
        this.scaleFunction = scaleFunction;
    }

    private TriFunction<Integer, Double, Double, Duration> expireFunction = (i, x, y) -> Duration.seconds(1);

    /**
     * Expire function is used to obtain expire time for particles.
     *
     * @param expireFunction function to supply expire time
     */
    public final void setExpireFunction(TriFunction<Integer, Double, Double, Duration> expireFunction) {
        this.expireFunction = expireFunction;
    }

    private TriFunction<Integer, Double, Double, BlendMode> blendFunction = (i, x, y) -> BlendMode.ADD;

    /**
     * Blend function is used to obtain blend mode for particles.
     *
     * @param blendFunction blend supplier function
     */
    public final void setBlendFunction(TriFunction<Integer, Double, Double, BlendMode> blendFunction) {
        this.blendFunction = blendFunction;
    }

    private Image sourceImage = null;

    /**
     * Set source image for this emitter to produce particles.
     *
     * @param sourceImage the image
     * @defaultValue null
     */
    public void setSourceImage(Image sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * Emission rate accumulator. Default value 1.0
     * so that when emitter starts working, it will emit in the same frame
     */
    private double rateAC = 1.0;

    /**
     * @return number of particles being spawned per emission
     */
    public final int getNumParticles() {
        return numParticles;
    }

    /**
     * Set number of particles being spawned per emission.
     *
     * @param numParticles number of particles
     */
    public final void setNumParticles(int numParticles) {
        this.numParticles = numParticles;
    }

    /**
     * Set the emission rate. The value will be effectively
     * clamped to [0..1].
     * <li> 1.0 - emission will occur every frame </li>
     * <li> 0.5 - emission will occur every 2nd frame </li>
     * <li> 0.33 - emission will occur every 3rd frame </li>
     * <li> 0.0 - emission will never occur </li>
     * etc.
     *
     * @param emissionRate emission rate
     */
    public final void setEmissionRate(double emissionRate) {
        this.emissionRate = emissionRate;
    }

    /**
     * Returns a value in [0..1).
     *
     * @return random value between 0 (incl) and 1 (excl)
     */
    protected final double rand() {
        return random.nextDouble();
    }

    /**
     * Returns a value in [min..max).
     *
     * @param min min bounds
     * @param max max bounds
     * @return a random value between min (incl) and max (excl)
     */
    protected final double rand(double min, double max) {
        return rand() * (max - min) + min;
    }

    /**
     * Emits {@link #numParticles} particles at x, y. This is
     * called every frame, however {@link #emissionRate} will
     * decide whether to spawn particles or not. If the emitter
     * is not ready, an empty list is returned.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return list of particles spawned
     */
    final List<Particle> emit(double x, double y) {
        rateAC += emissionRate;
        if (rateAC < 1 || emissionRate == 0) {
            return EMPTY;
        }

        rateAC = 0;
        return IntStream.range(0, numParticles)
                .mapToObj(i -> emit(i, x, y))
                .collect(Collectors.toList());
    }

    /**
     * Emits a single particle with index i. x and y
     * are coordinates of the particle entity this emitter is attached to.
     *
     * @param i particle index from 0 to {@link #numParticles}
     * @param x top left X of the particle entity
     * @param y top left Y of the particle entity
     * @return particle
     */
    private Particle emit(int i, double x, double y) {
        return new Particle(sourceImage, spawnPointFunction.apply(i, x, y),
                velocityFunction.apply(i, x, y),
                gravityFunction.get(),
                getRandomSize(),
                scaleFunction.apply(i, x, y),
                expireFunction.apply(i, x, y),
                colorFunction.get(),
                blendFunction.apply(i, x, y));
    }
}
