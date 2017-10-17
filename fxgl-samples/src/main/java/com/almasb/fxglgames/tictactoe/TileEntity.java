/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxglgames.tictactoe.control.TileControl;

/**
 * Instead of using generic Entity we add a few convenience methods.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileEntity extends Entity {

    public TileEntity(double x, double y) {
        setX(x);
        setY(y);
        addComponent(new TileValueComponent());

        getViewComponent().setView(new TileView(this), true);
        addControl(new TileControl());
    }

    public TileValue getValue() {
        return getComponent(TileValueComponent.class).getValue();
    }

    public TileControl getControl() {
        return getControl(TileControl.class);
    }
}
