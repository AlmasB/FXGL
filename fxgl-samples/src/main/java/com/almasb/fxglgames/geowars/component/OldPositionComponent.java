/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.ecs.component.ObjectComponent;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class OldPositionComponent extends ObjectComponent<Point2D> {
    public OldPositionComponent() {
        super(Point2D.ZERO);
    }
}
