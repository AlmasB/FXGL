/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.geowars.component.GraphicsComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GraphicsUpdateControl extends Control {

    @Override
    public void onUpdate(Entity entity, double tpf) {
        entity.getComponent(GraphicsComponent.class).getValue()
                .clearRect(0, 0, 1280, 720);
    }
}
