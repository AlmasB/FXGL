/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.scene.Viewport;

/**
 * A control that keeps an entity within the viewport.
 * Entities with physics enabled are not supported.
 * Do NOT use this control if viewport is bound to an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public class KeepOnScreenControl extends Control {

    private PositionComponent position;
    private BoundingBoxComponent bbox = null;

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
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
        bbox = Entities.getBBox(entity);

        viewport = FXGL.getApp().getGameScene().getViewport();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (bbox == null) {
            blockWithoutBBox();
        } else {
            blockWithBBox();
        }
    }

    private void blockWithoutBBox() {
        if (horizontally) {
            if (position.getX() < viewport.getX()) {
                position.setX(viewport.getX());
            } else if (position.getX() > viewport.getX() + viewport.getWidth()) {
                position.setX(viewport.getX() + viewport.getWidth());
            }
        }

        if (vertically) {
            if (position.getY() < viewport.getY()) {
                position.setY(viewport.getY());
            } else if (position.getY() > viewport.getY() + viewport.getHeight()) {
                position.setY(viewport.getY() + viewport.getHeight());
            }
        }
    }

    private void blockWithBBox() {
        if (horizontally) {
            if (bbox.getMinXWorld() < viewport.getX()) {
                position.setX(viewport.getX());
            } else if (bbox.getMaxXWorld() > viewport.getX() + viewport.getWidth()) {
                position.setX(viewport.getX() + viewport.getWidth() - bbox.getWidth());
            }
        }

        if (vertically) {
            if (bbox.getMinYWorld() < viewport.getY()) {
                position.setY(viewport.getY());
            } else if (bbox.getMaxYWorld() > viewport.getY() + viewport.getHeight()) {
                position.setY(viewport.getY() + viewport.getHeight() - bbox.getHeight());
            }
        }
    }
}
