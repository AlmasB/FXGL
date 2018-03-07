/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.util.BiConsumer;
import javafx.scene.canvas.GraphicsContext;

/**
 * Allows drawing directly to graphics context.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DrawableComponent extends Component {

    private BiConsumer<GraphicsContext, Entity> drawingFunction;

    public DrawableComponent(BiConsumer<GraphicsContext, Entity> drawingFunction) {
        this.drawingFunction = drawingFunction;
    }

    public void draw(GraphicsContext g) {
        drawingFunction.accept(g, getEntity());
    }
}
