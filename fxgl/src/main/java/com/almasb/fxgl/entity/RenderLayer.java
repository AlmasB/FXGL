/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
