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

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A general particle emitter with a few default settings.
 * Subclass implementations provide the actual logic for how
 * particles are emitted and their behavior.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class ParticleEmitter {

    private static final List<Particle> EMPTY = new ArrayList<>();

    private Random random = new Random();
    private int numParticles = 25;
    private double emissionRate = 1.0;

    private double sizeMin = 9;
    private double sizeMax = 12;

    /**
     *
     * @return minimum particle size
     */
    public final double getSizeMin() {
        return sizeMin;
    }

    /**
     *
     * @return maximum particle size
     */
    public final double getSizeMax() {
        return sizeMax;
    }

    /**
     *
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
     *
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
    protected abstract Particle emit(int i, double x, double y);
}
