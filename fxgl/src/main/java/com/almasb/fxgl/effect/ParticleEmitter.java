/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.effect;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.pool.Pool;
import com.almasb.fxgl.util.TriFunction;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A general particle emitter.
 * The configuration is done via setters, which allow
 * changing how the particle is emitted and rendered.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ParticleEmitter {

    static {
        FXGL.getPooler().registerPool(Particle.class, new Pool<Particle>(256) {
            @Override
            protected Particle newObject() {
                return new Particle();
            }
        });
    }

    /**
     * Caches baked images in the form: startColor -> endColor -> [Image, 0..99]
     */
    private static final ObjectMap<Color, ObjectMap<Color, Image[]> > IMAGE_CACHE = new ObjectMap<>();

    /**
     * Adapted from http://wecode4fun.blogspot.co.uk/2015/07/particles.html (Roland C.)
     *
     * Snapshot an image out of a node, consider transparency.
     */
    private static Image createImage(Node node) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);

        int imageWidth = (int) node.getBoundsInLocal().getWidth();
        int imageHeight = (int) node.getBoundsInLocal().getHeight();

        WritableImage image = new WritableImage(imageWidth, imageHeight);

        Async.startFX(() -> {
            node.snapshot(parameters, image);
        }).await();

        return image;
    }

    /**
     * Adapted from http://wecode4fun.blogspot.co.uk/2015/07/particles.html (Roland C.)
     *
     * Pre-create images with various gradient colors and sizes.
     */
    private static Image[] preCreateImages(Color startColor, Color endColor) {
        // get number of images
        int count = 100;

        // create linear gradient lookup image: lifespan 0 -> lifespan max
        double width = count;
        Stop[] stops = new Stop[] {
                new Stop(0, Color.BLACK.deriveColor(1, 1, 1, 0.0)),
                new Stop(0.3, endColor),
                new Stop(0.9, startColor),
                new Stop(1, startColor)
        };

        Rectangle rectangle = new Rectangle(width, 1,
                new LinearGradient(0, 0, width, 0, false, CycleMethod.NO_CYCLE, stops)
        );

        Image lookupImage = createImage(rectangle);

        Image[] list = new Image[count];

        double radius = 10;

        for (int i = 0; i < count; i++) {

            // get color depending on lifespan
            Color color = lookupImage.getPixelReader().getColor(i, 0);

            // create gradient image with given color
            Circle ball = new Circle(radius);
            //Line ball = new Line(0, 0, 0, radius);

            RadialGradient gradient1 = new RadialGradient(0, 0, 0, 0,
                    radius, false, CycleMethod.NO_CYCLE,
                    new Stop(0, color.deriveColor(1, 1, 1, 1)),
                    new Stop(1, color.deriveColor(1, 1, 1, 0))
            );

            //ball.setStroke(gradient1);
            ball.setFill(gradient1);

            list[i] = createImage(ball);
        }

        return list;
    }

    /**
     * @param index in range [0..99]
     * @return cached image based on start, end colors and interpolation value
     */
    static Image getCachedImage(Color startColor, Color endColor, int index) {

        ObjectMap<Color, Image[]> map = IMAGE_CACHE.get(startColor);
        if (map == null) {
            map = new ObjectMap<>();
            IMAGE_CACHE.put(startColor, map);
        }

        Image[] images = map.get(endColor);
        if (images == null) {
            images = preCreateImages(startColor, endColor);
            map.put(endColor, images);
        }

        return images[index];
    }

    private Random random = FXGLMath.getRandom();
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

    private Paint startColor = Color.TRANSPARENT;
    private Paint endColor = Color.TRANSPARENT;

    public Paint getStartColor() {
        return startColor;
    }

    public void setStartColor(Paint startColor) {
        this.startColor = startColor;
    }

    public Paint getEndColor() {
        return endColor;
    }

    public void setEndColor(Paint endColor) {
        this.endColor = endColor;
    }

    public Paint getColor() {
        return startColor;
    }

    public void setColor(Paint startColor) {
        this.startColor = startColor;
        this.endColor = startColor;
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

    private BlendMode blendMode =  BlendMode.SRC_OVER;

    public BlendMode getBlendMode() {
        return blendMode;
    }

    /**
     * Blend function is used to obtain blend mode for particles.
     *
     * @param blendMode blend supplier function
     */
    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
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

    private Array<Particle> emissionParticles = new Array<>(false, numParticles);

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
    final Array<Particle> emit(double x, double y) {
        rateAC += emissionRate;
        if (rateAC < 1 || emissionRate == 0) {
            return Array.empty();
        }

        rateAC = 0;
        emissionParticles.clear();

        for (int i = 0; i < numParticles; i++) {
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
        Particle particle = FXGL.getPooler().get(Particle.class);
        particle.init(sourceImage, spawnPointFunction.apply(i, x, y),
                velocityFunction.apply(i, x, y),
                gravityFunction.get(),
                getRandomSize(),
                scaleFunction.apply(i, x, y),
                expireFunction.apply(i, x, y),
                getStartColor(),
                getEndColor(),
                blendMode);

        return particle;
    }
}
