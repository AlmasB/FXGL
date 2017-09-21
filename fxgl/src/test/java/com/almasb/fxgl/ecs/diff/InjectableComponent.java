/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs.diff;

import com.almasb.fxgl.ecs.Component;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.EntityTest;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class InjectableComponent extends Component {

    private EntityTest.CustomDataComponent component;

    @Override
    public void onAdded(Entity entity) {
        if (component == null || !"Inject".equals(component.getData())) {
            throw new RuntimeException("Injection failed!");
        }
    }
}
