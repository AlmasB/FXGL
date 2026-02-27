/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.entity.EntityTest;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class InjectableControl extends Component {

    private EntityTest.CustomDataComponent component;

    private EntityTest.CustomDataControl control;

    @Override
    public void onAdded() {
        if (component == null || !"Inject".equals(component.getData())) {
            throw new RuntimeException("Injection failed!");
        }

        if (control == null || !"InjectControl".equals(control.getData())) {
            throw new RuntimeException("Injection failed!");
        }
    }

    @Override
    public void onUpdate(double tpf) {

    }
}
