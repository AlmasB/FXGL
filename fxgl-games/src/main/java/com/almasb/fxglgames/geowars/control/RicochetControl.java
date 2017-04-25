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
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxglgames.geowars.GeoWarsApp;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(BoundingBoxComponent.class)
public class RicochetControl extends AbstractControl {

    private BoundingBoxComponent bbox;

    @Override
    public void onAdded(Entity entity) {
        bbox = Entities.getBBox(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

        if (bbox.getMinXWorld() < 0 || bbox.getMaxXWorld() > FXGL.getApp().getWidth()
                || bbox.getMinYWorld() < 0 || bbox.getMaxYWorld() > FXGL.getApp().getHeight()) {

            Point2D direction = FXGL.<GeoWarsApp>getAppCast()
                    .getPlayer()
                    .getCenter()
                    .subtract(bbox.getCenterWorld());

            entity.getControlUnsafe(ProjectileControl.class).setDirection(direction);
        }
    }
}
