/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JavaAnonymousClassTest {

    private Entity entity;

    @BeforeEach
    public void setUp() {
        entity = new Entity();
    }

    @Test
    public void testAnonymousComponent() {
        assertThrows(IllegalArgumentException.class, () -> entity.addComponent(new Component() {}));
    }

    @Test
    public void testAnonymousControl() {
        assertThrows(IllegalArgumentException.class, () -> entity.addControl(new Control() {
                @Override
                public void onUpdate(Entity entity, double tpf) { }
        }));
    }
}
