/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.entity.components.ObjectComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GoldMineComponent extends ObjectComponent<GoldMine> {

    public GoldMineComponent() {
        super(new GoldMine());
    }
}
