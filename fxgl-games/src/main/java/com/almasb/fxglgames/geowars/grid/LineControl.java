/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxglgames.geowars.component.GraphicsComponent;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class LineControl extends AbstractControl {

    private PointMass end1, end2;
    private GraphicsContext g;

    public LineControl(PointMass end1, PointMass end2) {
        this.end1 = end1;
        this.end2 = end2;
    }

    @Override
    public void onAdded(Entity entity) {
        g = entity.getComponentUnsafe(GraphicsComponent.class).getValue();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        g.strokeLine(end1.getPosition().x, end1.getPosition().y,
                end2.getPosition().x, end2.getPosition().y);
    }
}
