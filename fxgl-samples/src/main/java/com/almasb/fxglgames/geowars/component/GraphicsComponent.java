/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.entity.component.ObjectComponent;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GraphicsComponent extends ObjectComponent<GraphicsContext> {
    public GraphicsComponent(GraphicsContext g) {
        super(g);
    }
}
