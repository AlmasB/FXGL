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
package com.almasb.fxgl.entity;

/**
 * Represents a layer which is used to group objects being rendered.
 * Layers are rendered from lower index value to higher index value.
 * Objects with higher index value will be rendered on top of objects
 * with lower index value.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface RenderLayer {

    /**
     * Returns a human readable name for this layer.
     *
     * @return layer name
     */
    String name();

    /**
     * Returns index value for this layer.
     *
     * @return layer index
     */
    int index();

    /**
     * @return string representation of render layer
     */
    default String asString() {
        return name() + "(" + index() + ")";
    }

    /**
     * Default render layer for entities with no specified
     * render layer.
     * Note: this is the highest layer that can be used.
     */
    RenderLayer TOP = new RenderLayer() {
        @Override
        public String name() {
            return "TOP";
        }

        @Override
        public int index() {
            return Integer.MAX_VALUE;
        }
    };

    /**
     * Render layer for background.
     * Note: value of 1000 leaves some scope for using parallax backgrounds.
     */
    RenderLayer BACKGROUND = new RenderLayer() {
        @Override
        public String name() {
            return "BACKGROUND";
        }

        @Override
        public int index() {
            return 1000;
        }
    };
}
