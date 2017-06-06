/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.ecs.component.ObjectComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BackpackComponent extends ObjectComponent<Backpack> {

    public BackpackComponent() {
        super(new Backpack());
    }
}
