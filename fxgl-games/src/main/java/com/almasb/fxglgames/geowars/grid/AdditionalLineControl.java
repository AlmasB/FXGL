/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxglgames.geowars.component.GraphicsComponent;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AdditionalLineControl extends Control {

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
        g = entity.getComponent(GraphicsComponent.class).getValue();
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