/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * Vignette effect node.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class Vignette extends Parent {

    private Rectangle region;

    /**
     * Constructs new vignette effect with given values
     *
     * @param width full width of vignette effect
     * @param height full height of vignette effect
     * @param radius approx. radius of the visible area (gradient extent)
     */
    public Vignette(int width, int height, double radius) {
        region = new Rectangle(width, height);
        this.radius = radius;

        applyChanges();

        getChildren().add(region);
    }

    /**
     * This must be called on any property change since
     * gradient is effectively immutable.
     */
    private void applyChanges() {
        region.setFill(new RadialGradient(0, 0, region.getWidth() / 2, region.getHeight() / 2, radius,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.color(color.getRed(), color.getGreen(), color.getBlue(), intensity))));
    }

    private double intensity = 1.0;

    /**
     * @return effect intensity in range [0..1]
     */
    public double getIntensity() {
        return intensity;
    }

    /**
     * Set effect intensity.
     * The value must be in range [0..1].
     *
     * @param intensity effect intensity
     * @throws IllegalArgumentException if the value is outside [0..1]
     */
    public void setIntensity(double intensity) {
        if (intensity < 0 || intensity > 1)
            throw new IllegalArgumentException("Intensity must be in range [0..1]. Value: " + intensity);

        this.intensity = intensity;
        applyChanges();
    }

    private double radius;

    /**
     * @return approx. radius defining the extents of color gradient
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Set gradient radius.
     *
     * @param radius approx. radius defining the extents of color gradient
     */
    public void setRadius(double radius) {
        this.radius = radius;
        applyChanges();
    }

    private Color color = Color.BLACK;

    /**
     * @return vignette color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set effect color.
     *
     * @param color vignette color
     */
    public void setColor(Color color) {
        this.color = color;
        applyChanges();
    }
}