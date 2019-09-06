/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.reflect;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TestSuperClass {

    public static Class<?> lowLevelFunction() {
        return ReflectionUtils.getCallingClass(TestSuperClass.class, "lowLevelFunction");
    }

    public static class TestSubClass extends TestSuperClass {

        public static Class<?> highLevelFunction() {
            return lowLevelFunction();
        }
    }

    public static class NoSubClass {
        public static Class<?> highLevelFunction() {
            return lowLevelFunction();
        }
    }
}
