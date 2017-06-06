/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture;

import com.almasb.fxgl.core.Disposable;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VerticalDirection;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Represents a 2D image which can be set as view for an entity.
 * The size ratio and viewport can be modified as necessary.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @apiNote This is essentially a wrapper around {@link javafx.scene.image.ImageView}
 */
public class Texture extends ImageView implements Disposable {

    /**
     * Constructs new texture from given image.
     *
     * @param image the JavaFX image data
     */
    public Texture(Image image) {
        super(image);
    }

    /**
     * Converts the texture to animated texture using
     * the whole texture as a single animation channel.
     * Must be in 1 row.
     *
     * @param frames   number of frames in sprite sheet
     * @param duration overall duration (for all frames) of the animation
     * @return new AnimatedTexture
     */
    public final AnimatedTexture toAnimatedTexture(int frames, Duration duration) {
        return toAnimatedTexture(new AnimationChannel(
                getImage(),
                frames, (int) getImage().getWidth() / frames, (int) getImage().getHeight(),
                duration, 0, frames-1)
        );
    }

    /**
     * Converts the texture to animated texture.
     *
     * @param defaultChannel the default channel
     * @return new AnimatedTexture
     */
    public final AnimatedTexture toAnimatedTexture(AnimationChannel defaultChannel) {
        return new AnimatedTexture(defaultChannel);
    }

    /**
     * Call this to create a new texture if you are
     * planning to use the same image as graphics
     * for multiple entities.
     * This is required because same Node can only have 1 parent.
     * <p>
     * Do NOT invoke on instances of StaticAnimatedTexture or
     * AnimatedTexture, use {@link #toAnimatedTexture(int, Duration)}
     * or {@link #toAnimatedTexture(AnimationChannel)} instead.
     *
     * @return new Texture with same image
     */
    public final Texture copy() {
        return new Texture(getImage());
    }

    /**
     * Given a rectangular area, produces a sub-texture of
     * this texture.
     * <p>
     * Rectangle cannot cover area outside of the original texture
     * image.
     *
     * @param area area of the original texture that represents sub-texture
     * @return sub-texture
     */
    public final Texture subTexture(Rectangle2D area) {
        int minX = (int) area.getMinX();
        int minY = (int) area.getMinY();
        int maxX = (int) area.getMaxX();
        int maxY = (int) area.getMaxY();

        if (minX < 0)
            throw new IllegalArgumentException("minX value of sub-texture cannot be negative");
        if (minY < 0)
            throw new IllegalArgumentException("minY value of sub-texture cannot be negative");
        if (maxX > getImage().getWidth())
            throw new IllegalArgumentException("maxX value of sub-texture cannot be greater than image width");
        if (maxY > getImage().getHeight())
            throw new IllegalArgumentException("maxY value of sub-texture cannot be greater than image height");

        PixelReader pixelReader = getImage().getPixelReader();
        WritableImage image = new WritableImage(maxX - minX, maxY - minY);
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                Color color = pixelReader.getColor(x, y);
                pixelWriter.setColor(x - minX, y - minY, color);
            }
        }

        return new Texture(image);
    }

    /**
     * Generates a new texture which combines this and given texture.
     * The given texture is appended based on the direction provided.
     *
     * @param other the texture to append to this one
     * @param direction the direction to append from
     * @return new combined texture
     */
    public final Texture superTexture(Texture other, HorizontalDirection direction) {
        Image leftImage;
        Image rightImage;

        if (direction == HorizontalDirection.LEFT) {
            leftImage = other.getImage();
            rightImage = this.getImage();
        } else {
            leftImage = this.getImage();
            rightImage = other.getImage();
        }

        int width = (int) (leftImage.getWidth() + rightImage.getWidth());
        int height = (int) Math.max(leftImage.getHeight(), rightImage.getHeight());

        PixelReader leftReader = leftImage.getPixelReader();
        PixelReader rightReader = rightImage.getPixelReader();
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color;
                if (x < leftImage.getWidth()) {
                    if (y < leftImage.getHeight()) {
                        color = leftReader.getColor(x, y);
                    } else {
                        color = Color.TRANSPARENT;
                    }
                } else {
                    if (y < rightImage.getHeight()) {
                        color = rightReader.getColor(x - (int)leftImage.getWidth(), y);
                    } else {
                        color = Color.TRANSPARENT;
                    }
                }

                pixelWriter.setColor(x, y, color);
            }
        }

        return new Texture(image);
    }

    /**
     * Generates a new texture which combines this and given texture.
     * The given texture is appended based on the direction provided.
     *
     * @param other the texture to append to this one
     * @param direction the direction to append from
     * @return new combined texture
     */
    public final Texture superTexture(Texture other, VerticalDirection direction) {
        Image topImage;
        Image bottomImage;

        if (direction == VerticalDirection.DOWN) {
            topImage = this.getImage();
            bottomImage = other.getImage();
        } else {
            topImage = other.getImage();
            bottomImage = this.getImage();
        }

        int width = (int) Math.max(topImage.getWidth(), bottomImage.getWidth());
        int height = (int) (topImage.getHeight() + bottomImage.getHeight());

        PixelReader topReader = topImage.getPixelReader();
        PixelReader bottomReader = bottomImage.getPixelReader();
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color;
                if (y < topImage.getHeight()) {
                    if (x < topImage.getWidth()) {
                        color = topReader.getColor(x, y);
                    } else {
                        color = Color.TRANSPARENT;
                    }
                } else {
                    if (x < bottomImage.getWidth()) {
                        color = bottomReader.getColor(x, y - (int)topImage.getHeight());
                    } else {
                        color = Color.TRANSPARENT;
                    }
                }

                pixelWriter.setColor(x, y, color);
            }
        }

        return new Texture(image);
    }

    /**
     * @return grayscale version of the texture
     */
    public final Texture toGrayscale() {
        int w = (int)getImage().getWidth();
        int h = (int)getImage().getHeight();

        PixelReader reader = getImage().getPixelReader();
        WritableImage image = new WritableImage(w, h);
        PixelWriter writer = image.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                writer.setColor(x, y, reader.getColor(x, y).grayscale());
            }
        }

        return new Texture(image);
    }

    /**
     * Discoloring is done via setting each pixel to white but
     * preserving opacity (alpha channel).
     *
     * @return texture with image discolored
     */
    public final Texture discolor() {
        int w = (int)getImage().getWidth();
        int h = (int)getImage().getHeight();

        PixelReader reader = getImage().getPixelReader();
        WritableImage image = new WritableImage(w, h);
        PixelWriter writer = image.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double opacity = reader.getColor(x, y).getOpacity();
                writer.setColor(x, y, Color.color(1, 1, 1, opacity));
            }
        }

        return new Texture(image);
    }

    /**
     * Multiplies this texture's pixel color with given color.
     *
     * @param color to use
     * @return new colorized texture
     */
    public final Texture multiplyColor(Color color) {
        int w = (int) getImage().getWidth();
        int h = (int) getImage().getHeight();

        PixelReader reader = getImage().getPixelReader();
        WritableImage coloredImage = new WritableImage(w, h);
        PixelWriter writer = coloredImage.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                Color c = reader.getColor(x, y);
                c = Color.color(
                        c.getRed() * color.getRed(),
                        c.getGreen() * color.getGreen(),
                        c.getBlue() * color.getBlue(),
                        c.getOpacity() * color.getOpacity()
                );

                writer.setColor(x, y, c);
            }
        }

        return new Texture(coloredImage);
    }

    /**
     * Colorizes this texture's pixels with given color.
     *
     * @param color to use
     * @return new colorized texture
     */
    public final Texture toColor(Color color) {
        Texture discolored = discolor();
        Texture colored = discolored.multiplyColor(color);
        discolored.dispose();

        return colored;
    }

    /**
     * Set texture data by copying it from other texture.
     *
     * @param other the texture to copy from
     */
    public final void set(Texture other) {
        setFitWidth(other.getFitWidth());
        setFitHeight(other.getFitHeight());
        setImage(other.getImage());
    }

    @Override
    public void dispose() {
        setImage(null);
    }

    @Override
    public String toString() {
        return "Texture [fitWidth=" + getFitWidth() + ", fitHeight=" + getFitHeight() + "]";
    }
}
