/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class InjectInTest {

    /**
     * @param lookup the lookup created by the module where class of instance lives
     * @param instance object to inject to
     * @param fieldName field name of instance
     * @param fieldValue value to inject
     */
    public static void inject(MethodHandles.Lookup lookup, Object instance, String fieldName, Object fieldValue) {
        try {
            Class<?> clazz = instance.getClass();
            Field field = clazz.getDeclaredField(fieldName);

            VarHandle handle = MethodHandles.privateLookupIn(clazz, lookup).unreflectVarHandle(field);

            handle.set(instance, fieldValue);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
