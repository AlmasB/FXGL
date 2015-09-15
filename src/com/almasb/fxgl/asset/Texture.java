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
package com.almasb.fxgl.asset;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Represents a 2D image which can be set as graphics for an entity.
 * The size ratio and viewport can be modified as necessary
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @apiNote This is essentially a wrapper around {@link javafx.scene.image.ImageView}
 */
public class Texture extends ImageView {

    /**
     * Prevent instantiation outside FXGL
     *
     * @param image the JavaFX image data
     */
    /*package-private*/ Texture(Image image) {
        super(image);
    }

    /**
     * Converts the texture to animated texture
     *
     * @param frames number of frames in spritesheet
     * @param duration overall duration (for all frames) of the animation
     * @return new StaticAnimatedTexture
     */
    public final StaticAnimatedTexture toStaticAnimatedTexture(int frames, Duration duration) {
        StaticAnimatedTexture texture = new StaticAnimatedTexture(getImage(), frames, duration);
        return texture;
    }

    /**
     * Call this to create a new texture if you are
     * planning to use the same image as graphics
     * for multiple entities. This is required because
     * same Node can only have 1 parent
     *
     * @return new Texture with same image
     */
    public final Texture copy() {
        return new Texture(getImage());
    }

    /**
     * Given a rectangular area, produces a sub-texture of
     * this texture.
     *
     * Rectangle cannot cover area outside of the original texture
     * image.
     *
     * @param area
     * @return
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Texture [fitWidth=");
        builder.append(getFitWidth());
        builder.append(", fitHeight=");
        builder.append(getFitHeight());
        builder.append("]");
        return builder.toString();
    }
}
