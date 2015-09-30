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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A general particle emitter with a few default settings.
 * Subclass implementations provide the actual logic for how
 * particles are emitted and their behavior.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public abstract class ParticleEmitter {

    private static final List<Particle> EMPTY = new ArrayList<>();

    private Random random = new Random();
    private int numParticles = 25;
    private double emissionRate = 1.0;

    /**
     * Emission rate accumulator. Default value 1.0
     * so that when emitter starts working, it will emit in the same frame
     */
    private double rateAC = 1.0;

    /**
     *
     * @return number of particles being spawned per emission
     */
    public final int getNumParticles() {
        return numParticles;
    }

    /**
     * Set number of particles being spawned per emission.
     *
     * @param numParticles
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
     * @param emissionRate
     */
    public final void setEmissionRate(double emissionRate) {
        this.emissionRate = emissionRate;
    }

    /**
     * Returns a value in [0..1).
     *
     * @return
     */
    protected final double rand() {
        return random.nextDouble();
    }

    /**
     * Returns a value in [min..max).
     *
     * @param min
     * @param max
     * @return
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
     * @param x
     * @param y
     * @return
     */
    /*package-private*/ final List<Particle> emit(double x, double y) {
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
     * are coords of the particle entity this emitter is attached to.
     *
     * @param i particle index from 0 to {@link #numParticles}
     * @param x top left X of the particle entity
     * @param y top left Y of the particle entity
     * @return particle
     */
    protected abstract Particle emit(int i, double x, double y);
}
