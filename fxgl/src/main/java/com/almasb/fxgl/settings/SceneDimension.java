/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.settings;

import javafx.beans.NamedArg;
import javafx.geometry.Dimension2D;

/**
 * A 2d dimension that describes scene resolution.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class SceneDimension extends Dimension2D {

    /**
     * Constructs a <code>SceneDimension</code> with the specified width and
     * height.
     *
     * @param width  the width
     * @param height the height
     */
    public SceneDimension(@NamedArg("width") double width, @NamedArg("height") double height) {
        super(width, height);
    }

    @Override
    public String toString() {
        return String.format("%.0fx%.0f", getWidth(), getHeight());
    }
}
