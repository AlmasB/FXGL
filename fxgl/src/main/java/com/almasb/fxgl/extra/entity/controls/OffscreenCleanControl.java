/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.controls;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.scene.Viewport;

/**
 * Control that removes an entity if it is outside of the visible area of the viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class OffscreenCleanControl extends Component {

    private Viewport viewport;

    public OffscreenCleanControl() {
        viewport = FXGL.getApp().getGameScene().getViewport();
    }

    @Override
    public void onUpdate(double v) {
        if (entity.getBoundingBoxComponent().isOutside(viewport.getVisibleArea())) {
            entity.removeFromWorld();
        }
    }
}
