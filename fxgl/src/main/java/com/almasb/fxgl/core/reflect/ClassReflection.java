/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.reflect;

/** Utilities for Class reflection.
 * @author nexsoftware */
public final class ClassReflection {

    /** Returns the Class object associated with the class or interface with the supplied string name. */
    public static Class forName(String name) throws ReflectionException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException("Class not found: " + name, e);
        }
    }

    /** Returns the simple name of the underlying class as supplied in the source code. */
    public static String getSimpleName(Class c) {
        return c.getSimpleName();
    }

    /** Determines if the supplied Object is assignment-compatible with the object represented by supplied Class. */
    public static boolean isInstance(Class c, Object obj) {
        return c.isInstance(obj);
    }

    /** Determines if the class or interface represented by first Class parameter is either the same as, or is a superclass or
     * superinterface of, the class or interface represented by the second Class parameter. */
    public static boolean isAssignableFrom(Class c1, Class c2) {
        return c1.isAssignableFrom(c2);
    }

    /** Creates a new instance of the class represented by the supplied Class. */
    public static <T> T newInstance(Class<T> c) throws ReflectionException {
        try {
            return c.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), e);
        }
    }

    /** Returns a {@link Constructor} that represents the public constructor for the supplied class which takes the supplied
     * parameter types. */
    public static Constructor getConstructor(Class c, Class... parameterTypes) throws ReflectionException {
        try {
            return new Constructor(c.getConstructor(parameterTypes));
        } catch (SecurityException e) {
            throw new ReflectionException("Security violation occurred while getting constructor for class: '" + c.getName() + "'.",
                    e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException("Constructor not found for class: " + c.getName(), e);
        }
    }

    /** Returns a {@link Constructor} that represents the constructor for the supplied class which takes the supplied parameter
     * types. */
    public static Constructor getDeclaredConstructor(Class c, Class... parameterTypes) throws ReflectionException {
        try {
            return new Constructor(c.getDeclaredConstructor(parameterTypes));
        } catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting constructor for class: " + c.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException("Constructor not found for class: " + c.getName(), e);
        }
    }

    /** Returns an array of {@link Field} containing the public fields of the class represented by the supplied Class. */
    public static Field[] getFields(Class c) {
        java.lang.reflect.Field[] fields = c.getFields();
        Field[] result = new Field[fields.length];
        for (int i = 0, j = fields.length; i < j; i++) {
            result[i] = new Field(fields[i]);
        }
        return result;
    }

    /** Returns a {@link Field} that represents the specified public member field for the supplied class. */
    public static Field getField(Class c, String name) throws ReflectionException {
        try {
            return new Field(c.getField(name));
        } catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting field: " + name + ", for class: " + c.getName(), e);
        } catch (NoSuchFieldException e) {
            throw new ReflectionException("Field not found: " + name + ", for class: " + c.getName(), e);
        }
    }

    /** Returns an {@link Annotation} object reflecting the annotation provided, or null if this class doesn't have such an
     * annotation. This is a convenience function if the caller knows already which annotation type he's looking for. */
    public static Annotation getAnnotation(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation annotation = c.getAnnotation(annotationType);
        if (annotation != null) return new Annotation(annotation);
        return null;
    }
}
