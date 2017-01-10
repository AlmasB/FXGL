/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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
package org.jbox2d.common;

import com.almasb.fxgl.core.math.Vec2;

/**
 * This is the viewport transform used from drawing. Use yFlip if you are drawing from the top-left
 * corner.
 *
 * @author Daniel
 */
public interface IViewportTransform {

    /**
     * @return if the transform flips the y axis
     */
    boolean isYFlip();

    /**
     * @param yFlip if we flip the y axis when transforming
     */
    void setYFlip(boolean yFlip);

    /**
     * This is the half-width and half-height. This should be the actual half-width and half-height,
     * not anything transformed or scaled. Not a copy.
     */
    Vec2 getExtents();

    /**
     * This sets the half-width and half-height. This should be the actual half-width and half-height,
     * not anything transformed or scaled.
     */
    void setExtents(Vec2 extents);

    /**
     * This sets the half-width and half-height of the viewport. This should be the actual half-width
     * and half-height, not anything transformed or scaled.
     */
    void setExtents(float halfWidth, float halfHeight);

    /**
     * center of the viewport. Not a copy.
     */
    Vec2 getCenter();

    /**
     * sets the center of the viewport.
     */
    void setCenter(Vec2 pos);

    /**
     * sets the center of the viewport.
     */
    void setCenter(float x, float y);

    /**
     * Sets the transform's center to the given x and y coordinates, and using the given scale.
     */
    void setCamera(float x, float y, float scale);

    /**
     * Transforms the given directional vector by the viewport transform (not positional)
     */
    void getWorldVectorToScreen(Vec2 world, Vec2 screen);


    /**
     * Transforms the given directional screen vector back to the world direction.
     */
    void getScreenVectorToWorld(Vec2 screen, Vec2 world);

    Mat22 getMat22Representation();


    /**
     * takes the world coordinate (world) puts the corresponding screen coordinate in screen. It
     * should be safe to give the same object as both parameters.
     */
    void getWorldToScreen(Vec2 world, Vec2 screen);


    /**
     * takes the screen coordinates (screen) and puts the corresponding world coordinates in world. It
     * should be safe to give the same object as both parameters.
     */
    void getScreenToWorld(Vec2 screen, Vec2 world);

    /**
     * Multiplies the viewport transform by the given Mat22
     */
    void mulByTransform(Mat22 transform);
}
