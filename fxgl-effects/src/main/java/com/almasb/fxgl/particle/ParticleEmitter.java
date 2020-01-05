/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.particle;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.UnorderedArray;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.core.util.Consumer;
import com.almasb.fxgl.core.util.Function;
import com.almasb.fxgl.core.util.Supplier;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.Interpolator;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

/**
 * A general particle emitter.
 * The configuration is done via setters, which allow
 * changing how the particle is emitted and rendered.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class ParticleEmitter {

    private IntegerProperty numParticles = new SimpleIntegerProperty(25);

    public IntegerProperty numParticlesProperty() {
        return numParticles;
    }

    /**
     * @return number of particles being spawned per emission
     */
    public int getNumParticles() {
        return numParticles.get();
    }

    /**
     * @param numParticles number of particles being spawned per emission
     */
    public void setNumParticles(int numParticles) {
        this.numParticles.set(numParticles);
    }

    private DoubleProperty emissionRate = new SimpleDoubleProperty(1.0);

    public DoubleProperty emissionRateProperty() {
        return emissionRate;
    }

    /**
     * @return emission rate effective value in [0..1]
     */
    public double getEmissionRate() {
        return emissionRate.get();
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
    public void setEmissionRate(double emissionRate) {
        this.emissionRate.set(emissionRate);
    }

    private int maxEmissions = Integer.MAX_VALUE;

    public int getMaxEmissions() {
        return maxEmissions;
    }

    public void setMaxEmissions(int maxEmissions) {
        this.maxEmissions = maxEmissions;
    }

    public boolean isFinished() {
        return emissions == maxEmissions;
    }

    private DoubleProperty minSize = new SimpleDoubleProperty(9.0);

    /**
     * @return minimum particle size
     */
    public double getMinSize() {
        return minSize.get();
    }

    public DoubleProperty minSizeProperty() {
        return minSize;
    }

    public void setMinSize(double minSize) {
        this.minSize.set(minSize);
    }

    private DoubleProperty maxSize = new SimpleDoubleProperty(12.0);

    /**
     * @return maximum particle size
     */
    public double getMaxSize() {
        return maxSize.get();
    }

    public DoubleProperty maxSizeProperty() {
        return maxSize;
    }

    public void setMaxSize(double maxSize) {
        this.maxSize.set(maxSize);
    }

    /**
     * Set size to particles.
     * The created size will be a random value
     * between min (incl) and max (excl).
     *
     * @param min minimum size
     * @param max maximum size
     */
    public void setSize(double min, double max) {
        setMinSize(min);
        setMaxSize(max);
    }

    /**
     * @return random size between min and max size
     */
    private double getRandomSize() {
        return FXGLMath.random(getMinSize(), getMaxSize());
    }

    private ObjectProperty<Paint> startColor = new SimpleObjectProperty<>(Color.TRANSPARENT);

    private ObjectProperty<Paint> endColor = new SimpleObjectProperty<>(Color.TRANSPARENT);

    public Paint getStartColor() {
        return startColor.get();
    }

    public ObjectProperty<Paint> startColorProperty() {
        return startColor;
    }

    public void setStartColor(Paint startColor) {
        this.startColor.set(startColor);
    }

    public Paint getEndColor() {
        return endColor.get();
    }

    public ObjectProperty<Paint> endColorProperty() {
        return endColor;
    }

    public void setEndColor(Paint endColor) {
        this.endColor.set(endColor);
    }

    public void setColor(Paint color) {
        setStartColor(color);
        setEndColor(color);
    }

    private ObjectProperty<BlendMode> blendMode = new SimpleObjectProperty<>(BlendMode.SRC_OVER);

    public BlendMode getBlendMode() {
        return blendMode.get();
    }

    public ObjectProperty<BlendMode> blendModeProperty() {
        return blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        this.blendMode.set(blendMode);
    }

    private ObjectProperty<Interpolator> interpolator = new SimpleObjectProperty<>(Interpolator.LINEAR);

    public Interpolator getInterpolator() {
        return interpolator.get();
    }

    public ObjectProperty<Interpolator> interpolatorProperty() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator.set(interpolator);
    }

    private BooleanProperty allowParticleRotation = new SimpleBooleanProperty(false);

    public boolean isAllowParticleRotation() {
        return allowParticleRotation.get();
    }

    public BooleanProperty allowParticleRotationProperty() {
        return allowParticleRotation;
    }

    public void setAllowParticleRotation(boolean allowParticleRotation) {
        this.allowParticleRotation.set(allowParticleRotation);
    }

    /* FUNCTION CONFIGURATORS */

    private Consumer<Particle> control = null;

    public Consumer<Particle> getControl() {
        return control;
    }

    /**
     * Set control function to override velocity and acceleration
     * of each particle.
     */
    public void setControl(Consumer<Particle> control) {
        this.control = control;
    }

    private Function<Double, Point2D> parametricEquation = null;

    public Function<Double, Point2D> getParametricEquation() {
        return parametricEquation;
    }

    public void setParametricEquation(Function<Double, Point2D> parametricEquation) {
        this.parametricEquation = parametricEquation;
    }

    private Supplier<Point2D> accelerationFunction = () -> Point2D.ZERO;

    /**
     *
     * @return gravity function
     */
    public Supplier<Point2D> getAccelerationFunction() {
        return accelerationFunction;
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
    public void setAccelerationFunction(Supplier<Point2D> gravityFunction) {
        this.accelerationFunction = gravityFunction;
    }

    private Function<Integer, Point2D> velocityFunction = (i) -> Point2D.ZERO;

    /**
     * Set initial velocity function. Particles when spawned will use the function
     * to obtain initial velocity.
     *
     * @param velocityFunction the velocity function
     */
    public void setVelocityFunction(Function<Integer, Point2D> velocityFunction) {
        this.velocityFunction = velocityFunction;
    }

    private Function<Integer, Point2D> spawnPointFunction = (i) -> Point2D.ZERO;

    /**
     * Particles will use the function to obtain spawn points.
     * These spawn points are local to the entity (to which this emitter is attached) coordinate system.
     *
     * @param spawnPointFunction supplier of spawn points
     */
    public void setSpawnPointFunction(Function<Integer, Point2D> spawnPointFunction) {
        this.spawnPointFunction = spawnPointFunction;
    }

    private Function<Integer, Point2D> scaleOriginFunction = (i) -> Point2D.ZERO;

    /**
     * Set scale origin for each particle.
     */
    public void setScaleOriginFunction(Function<Integer, Point2D> scaleOriginFunction) {
        this.scaleOriginFunction = scaleOriginFunction;
    }

    private Function<Integer, Point2D> scaleFunction = (i) -> Point2D.ZERO;

    /**
     * Scale function defines how the size of particles change over time.
     *
     * @param scaleFunction scaling function
     */
    public void setScaleFunction(Function<Integer, Point2D> scaleFunction) {
        this.scaleFunction = scaleFunction;
    }

    private Supplier<Point2D> entityScaleFunction = () -> new Point2D(1, 1);

    public void setEntityScaleFunction(Supplier<Point2D> entityScaleFunction) {
        this.entityScaleFunction = entityScaleFunction;
    }

    private Function<Integer, Duration> expireFunction = (i) -> Duration.seconds(1);

    /**
     * Expire function is used to obtain expire time for particles.
     *
     * @param expireFunction function to supply expire time
     */
    public void setExpireFunction(Function<Integer, Duration> expireFunction) {
        this.expireFunction = expireFunction;
    }

    private Image sourceImage = null;

    /**
     * Set source image for this emitter to produce particles.
     * Default: null.
     *
     * @param sourceImage the image
     */
    public void setSourceImage(Image sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * Set source image from the texture for this emitter to produce particles.
     *
     * @param texture the texture whose image is used
     */
    public void setSourceImage(Texture texture) {
        setSourceImage(texture.getImage());
    }

    /**
     * Emission rate accumulator. Default value 1.0
     * so that when emitter starts working, it will emit in the same frame
     */
    private double rateAC = 1.0;

    /**
     * Number of times particles have been emitted.
     */
    private int emissions = 0;

    private Array<Particle> emissionParticles = new UnorderedArray<>(getNumParticles());

    /**
     * Emits {@link #numParticles} particles at x, y. This is
     * called every frame, however {@link #emissionRate} will
     * decide whether to spawn particles or not. If the emitter
     * is not ready, an empty list is returned.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return list of particles spawned
     * @implNote cached array is used, do not obtain ownership
     */
    Array<Particle> emit(double x, double y) {
        double rate = getEmissionRate();

        rateAC += rate;
        if (rateAC < 1 || rate == 0 || isFinished()) {
            return Array.empty();
        }

        rateAC = 0;
        emissions++;
        emissionParticles.clear();

        int num = getNumParticles();

        for (int i = 0; i < num; i++) {
            emissionParticles.add(emit(i, x, y));
        }

        return emissionParticles;
    }

    /**
     * Emits a single particle with index i.
     * X and Y are coordinates of the particle entity this emitter is attached to.
     *
     * @param i particle index from 0 to {@link #numParticles}
     * @param x top left X of the particle entity
     * @param y top left Y of the particle entity
     * @return particle
     */
    private Particle emit(int i, double x, double y) {
        Particle particle = Pools.obtain(Particle.class);

        particle.init(getControl(),
                sourceImage,
                spawnPointFunction.apply(i).add(x, y),
                velocityFunction.apply(i),
                accelerationFunction.get(),
                getRandomSize(),
                scaleOriginFunction.apply(i),
                scaleFunction.apply(i),
                entityScaleFunction.get(),
                expireFunction.apply(i),
                getStartColor(),
                getEndColor(),
                getBlendMode(),
                getInterpolator(),
                isAllowParticleRotation(),
                getParametricEquation());

        return particle;
    }
}
