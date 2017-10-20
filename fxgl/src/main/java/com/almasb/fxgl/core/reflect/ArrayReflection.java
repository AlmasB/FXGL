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
}
