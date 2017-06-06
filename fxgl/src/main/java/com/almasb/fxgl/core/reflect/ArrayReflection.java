/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.reflect;

/** Utilities for Array reflection.
 * @author nexsoftware */
public final class ArrayReflection {

    /** Creates a new array with the specified component type and length. */
    public static Object newInstance(Class c, int size) {
        return java.lang.reflect.Array.newInstance(c, size);
    }

    /** Returns the length of the supplied array. */
    public static int getLength(Object array) {
        return java.lang.reflect.Array.getLength(array);
    }

    /** Returns the value of the indexed component in the supplied array. */
    public static Object get(Object array, int index) {
        return java.lang.reflect.Array.get(array, index);
    }

    /** Sets the value of the indexed component in the supplied array to the supplied value. */
    public static void set(Object array, int index, Object value) {
        java.lang.reflect.Array.set(array, index, value);
    }

}
