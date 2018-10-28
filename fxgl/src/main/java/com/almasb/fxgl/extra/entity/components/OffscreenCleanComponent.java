/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.scene.Viewport;

/**
 * Removes an entity if it is outside of the visible area of the viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class OffscreenCleanComponent extends Component {

    private Viewport viewport;

    public OffscreenCleanComponent() {
        viewport = FXGL.getGameScene().getViewport();
    }

    @Override
    public void onUpdate(double tpf) {
        if (entity.getBoundingBoxComponent().isOutside(viewport.getVisibleArea())) {
            entity.removeFromWorld();
        }
    }
}
