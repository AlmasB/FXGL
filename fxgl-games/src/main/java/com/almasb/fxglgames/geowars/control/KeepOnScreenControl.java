/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.Required;
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
public class KeepOnScreenControl extends AbstractControl {

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
