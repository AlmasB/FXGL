/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JavaAnonymousClassTest {

    private Entity entity;

    @Before
    public void setUp() {
        entity = new Entity();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnonymousComponent() {
        entity.addComponent(new Component() {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnonymousControl() {
        entity.addControl(new Control() {
            @Override
            public void onUpdate(Entity entity, double tpf) {

            }
        });
    }
}
