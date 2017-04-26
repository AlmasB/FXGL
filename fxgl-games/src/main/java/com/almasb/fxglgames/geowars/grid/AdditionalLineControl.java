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

package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxglgames.geowars.component.GraphicsComponent;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AdditionalLineControl extends AbstractControl {

    private PointMass end11, end12, end21, end22;
    private GraphicsContext g;

    private Vec2 position1 = new Vec2();
    private Vec2 position2 = new Vec2();

    public AdditionalLineControl(PointMass end11, PointMass end12,
                                 PointMass end21, PointMass end22) {
        this.end11 = end11;
        this.end12 = end12;
        this.end21 = end21;
        this.end22 = end22;
    }

    @Override
    public void onAdded(Entity entity) {
        g = entity.getComponentUnsafe(GraphicsComponent.class).getValue();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        position1.x = end11.getPosition().x + (end12.getPosition().x - end11.getPosition().x) / 2;
        position1.y = end11.getPosition().y + (end12.getPosition().y - end11.getPosition().y) / 2;

        position2.x = end21.getPosition().x + (end22.getPosition().x - end21.getPosition().x) / 2;
        position2.y = end21.getPosition().y + (end22.getPosition().y - end21.getPosition().y) / 2;

        g.strokeLine(position1.x, position1.y, position2.x, position2.y);
    }
}