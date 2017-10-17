/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.entity.component.ObjectComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileValueComponent extends ObjectComponent<TileValue> {

    public TileValueComponent() {
        super(TileValue.NONE);
    }
}
