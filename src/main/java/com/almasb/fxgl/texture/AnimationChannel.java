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

package com.almasb.fxgl.texture;

import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

/**
 * Represents one of the animation channels from sprite sheet.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface AnimationChannel {

    /**
     * Area to be used to select a sub-texture from a sprite sheet.
     *
     * @return area
     */
    Rectangle2D area();

    /**
     * @return number of frames in this animation
     */
    int frames();

    /**
     * @return total duration of the animation
     */
    Duration duration();

    /**
     * @return name of the animation
     */
    String name();

    /**
     * Computes frame width based on the area width
     * and number of frames.
     *
     * @return frame width
     */
    default double computeFrameWidth() {
        return area().getWidth() / frames();
    }

    /**
     * Computes frame height based on the area height.
     *
     * @return frame height
     */
    default double computeFrameHeight() {
        return area().getHeight();
    }

    /**
     * Computes the viewport for given frame. Frames
     * start from 0.
     *
     * @param frame frame number
     * @return viewport area for frame
     */
    default Rectangle2D computeViewport(int frame) {
        double frameW = computeFrameWidth();
        Rectangle2D area = area();
        return new Rectangle2D(frame * frameW, area.getMinY(), frameW, area.getHeight());
    }
}
