/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.scene.Viewport;

/**
 * Control that removes an entity if it is outside of the visible area of the viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(BoundingBoxComponent.class)
public class OffscreenCleanControl extends AbstractControl {

    private Viewport viewport;
    private BoundingBoxComponent bbox;

    public OffscreenCleanControl() {
        viewport = FXGL.getApp().getGameScene().getViewport();
    }

    @Override
    public void onAdded(Entity entity) {
        bbox = Entities.getBBox(entity);
    }

    @Override
    public void onUpdate(Entity entity, double v) {
        if (bbox.isOutside(viewport.getVisibleArea())) {
            entity.removeFromWorld();
        }
    }
}
