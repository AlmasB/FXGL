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
public class RenderLayer {

    /**
     * Default render layer for entities with no specified
     * render layer.
     */
    public static final RenderLayer DEFAULT = new RenderLayer("DEFAULT", 100000);

    /**
     * Note: this is the highest layer that can be used.
     */
    public static final RenderLayer TOP = new RenderLayer("TOP", Integer.MAX_VALUE);

    /**
     * Note: this is the lowest layer that can be used.
     */
    public static final RenderLayer BOTTOM = new RenderLayer("BOTTOM", Integer.MIN_VALUE);

    /**
     * Convenience render layer for background.
     * Note: value of 1000 leaves some scope for using parallax backgrounds.
     */
    public static final RenderLayer BACKGROUND = new RenderLayer("BACKGROUND", 1000);

    private String name;
    private int index;

    @Deprecated
    public RenderLayer() {

    }

    public RenderLayer(int index) {
        this("NoName", index);
    }

    public RenderLayer(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Returns a human readable name for this layer.
     *
     * @return layer name
     */
    public String name() {
        return name;
    }

    /**
     * Returns index value for this layer.
     *
     * @return layer index
     */
    public int index() {
        return index;
    }

    @Override
    public String toString() {
        return name + "(" + index + ")";
    }
}
