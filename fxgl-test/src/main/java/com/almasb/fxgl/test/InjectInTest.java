/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.test;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class InjectInTest {

    public static void inject(Object instance, String fieldName, Object fieldValue) {
        try {
            var field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, fieldValue);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
