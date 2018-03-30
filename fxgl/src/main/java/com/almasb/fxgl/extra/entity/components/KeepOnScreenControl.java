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
 * A control that keeps an entity within the viewport.
 * Entities with physics enabled are not supported.
 * Do NOT use this control if viewport is bound to an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class KeepOnScreenControl extends Component {

    private Viewport viewport;

    private boolean horizontally;
    private boolean vertically;

    /**
     * @param horizontally keep on screen in X axis
     * @param vertically keep on screen in Y axis
     */
    public KeepOnScreenControl(boolean horizontally, boolean vertically) {
        this.horizontally = horizontally;
        this.vertically = vertically;
    }

    @Override
    public void onAdded() {
        viewport = FXGL.getApp().getGameScene().getViewport();
    }

    @Override
    public void onUpdate(double tpf) {
        blockWithBBox();
    }

    private void blockWithBBox() {
        if (horizontally) {
            if (getEntity().getX() < viewport.getX()) {
                getEntity().setX(viewport.getX());
            } else if (getEntity().getRightX() > viewport.getX() + viewport.getWidth()) {
                getEntity().setX(viewport.getX() + viewport.getWidth() - getEntity().getWidth());
            }
        }

        if (vertically) {
            if (getEntity().getY() < viewport.getY()) {
                getEntity().setY(viewport.getY());
            } else if (getEntity().getBottomY() > viewport.getY() + viewport.getHeight()) {
                getEntity().setY(viewport.getY() + viewport.getHeight() - getEntity().getHeight());
            }
        }
    }
}
