/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.entity.component.ViewComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class LaserHitControl extends Control {

    private EntityView view;

    @Override
    public void onAdded(Entity entity) {
        view = entity.getComponent(ViewComponent.class).getView();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        view.setOpacity(view.getOpacity() - tpf);

        if (view.getOpacity() <= 0) {
            entity.removeFromWorld();
        }
    }
}
