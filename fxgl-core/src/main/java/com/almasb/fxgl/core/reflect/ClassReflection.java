/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.reflect;

import java.lang.reflect.Modifier;

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

    /** Returns true if the class or interface represented by the supplied Class is a member class. */
    public static boolean isMemberClass(Class c) {
        return c.isMemberClass();
    }

    /** Returns true if the class or interface represented by the supplied Class is a static class. */
    public static boolean isStaticClass(Class c) {
        return Modifier.isStatic(c.getModifiers());
    }

    /** Determines if the supplied Class object represents an array class. */
    public static boolean isArray(Class c) {
        return c.isArray();
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

    /** Returns an array of {@link Constructor} containing the public constructors of the class represented by the supplied Class. */
    public static Constructor[] getConstructors(Class c) {
        java.lang.reflect.Constructor[] constructors = c.getConstructors();
        Constructor[] result = new Constructor[constructors.length];
        for (int i = 0, j = constructors.length; i < j; i++) {
            result[i] = new Constructor(constructors[i]);
        }
        return result;
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

    /** Returns an array of {@link Method} containing the public member methods of the class represented by the supplied Class. */
    public static Method[] getMethods(Class c) {
        java.lang.reflect.Method[] methods = c.getMethods();
        Method[] result = new Method[methods.length];
        for (int i = 0, j = methods.length; i < j; i++) {
            result[i] = new Method(methods[i]);
        }
        return result;
    }

    /** Returns a {@link Method} that represents the public member method for the supplied class which takes the supplied parameter
     * types. */
    public static Method getMethod(Class c, String name, Class... parameterTypes) throws ReflectionException {
        try {
            return new Method(c.getMethod(name, parameterTypes));
        } catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), e);
        }
    }

    /** Returns an array of {@link Method} containing the methods declared by the class represented by the supplied Class. */
    public static Method[] getDeclaredMethods(Class c) {
        java.lang.reflect.Method[] methods = c.getDeclaredMethods();
        Method[] result = new Method[methods.length];
        for (int i = 0, j = methods.length; i < j; i++) {
            result[i] = new Method(methods[i]);
        }
        return result;
    }

    /** Returns a {@link Method} that represents the method declared by the supplied class which takes the supplied parameter types. */
    public static Method getDeclaredMethod(Class c, String name, Class... parameterTypes) throws ReflectionException {
        try {
            return new Method(c.getDeclaredMethod(name, parameterTypes));
        } catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), e);
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

    /** Returns an array of {@link Field} objects reflecting all the fields declared by the supplied class. */
    public static Field[] getDeclaredFields(Class c) {
        java.lang.reflect.Field[] fields = c.getDeclaredFields();
        Field[] result = new Field[fields.length];
        for (int i = 0, j = fields.length; i < j; i++) {
            result[i] = new Field(fields[i]);
        }
        return result;
    }

    /** Returns a {@link Field} that represents the specified declared field for the supplied class. */
    public static Field getDeclaredField(Class c, String name) throws ReflectionException {
        try {
            return new Field(c.getDeclaredField(name));
        } catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting field: " + name + ", for class: " + c.getName(), e);
        } catch (NoSuchFieldException e) {
            throw new ReflectionException("Field not found: " + name + ", for class: " + c.getName(), e);
        }
    }

    /** Returns true if the supplied class includes an annotation of the given type. */
    public static boolean isAnnotationPresent(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        return c.isAnnotationPresent(annotationType);
    }

    /** Returns an array of {@link Annotation} objects reflecting all annotations declared by the supplied class, and inherited
     * from its superclass. Returns an empty array if there are none. */
    public static Annotation[] getAnnotations(Class c) {
        java.lang.annotation.Annotation[] annotations = c.getAnnotations();
        Annotation[] result = new Annotation[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            result[i] = new Annotation(annotations[i]);
        }
        return result;
    }

    /** Returns an {@link Annotation} object reflecting the annotation provided, or null if this class doesn't have such an
     * annotation. This is a convenience function if the caller knows already which annotation type he's looking for. */
    public static Annotation getAnnotation(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation annotation = c.getAnnotation(annotationType);
        if (annotation != null) return new Annotation(annotation);
        return null;
    }

    /** Returns an array of {@link Annotation} objects reflecting all annotations declared by the supplied class, or an empty
     * array if there are none. Does not include inherited annotations. */
    public static Annotation[] getDeclaredAnnotations(Class c) {
        java.lang.annotation.Annotation[] annotations = c.getDeclaredAnnotations();
        Annotation[] result = new Annotation[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            result[i] = new Annotation(annotations[i]);
        }
        return result;
    }

    /** Returns an {@link Annotation} object reflecting the annotation provided, or null if this class doesn't have such an
     * annotation. This is a convenience function if the caller knows already which annotation type he's looking for. */
    public static Annotation getDeclaredAnnotation(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation[] annotations = c.getDeclaredAnnotations();
        for (java.lang.annotation.Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationType)) return new Annotation(annotation);
        }
        return null;
    }

    public static Class[] getInterfaces(Class c) {
        return c.getInterfaces();
    }

}
