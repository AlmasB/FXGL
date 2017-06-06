/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.reflect;

import com.almasb.fxgl.core.collection.Array;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A collection of convenience methods to isolate reflection code.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ReflectionUtils {

    private ReflectionUtils() {}

    public static <A extends java.lang.annotation.Annotation> Map<A, Method>
        findMethods(Object instance, Class<A> annotationClass) {

        Map<A, Method> map = new HashMap<>();

        for (Method method : instance.getClass().getDeclaredMethods()) {
            A annotation = method.getDeclaredAnnotation(annotationClass);
            if (annotation != null) {
                map.put(annotation, method);
            }
        }

        return map;
    }

    public static <T, R, A extends java.lang.annotation.Annotation> Map<A, Function<T, R>>
        findMethodsMapToFunctions(Object instance, Class<A> annotationClass) {

        Map<A, Function<T, R>> map = new HashMap<>();

        findMethods(instance, annotationClass)
                .forEach((annotation, method) -> map.put(annotation, mapToFunction(instance, method)));

        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T, R, F extends Function<T, R>, A extends java.lang.annotation.Annotation> Map<A, F>
        findMethodsMapToFunctions(Object instance, Class<A> annotationClass, Class<F> functionClass) {

        Map<A, F> map = new HashMap<>();

        findMethods(instance, annotationClass)
                .forEach((annotation, method) -> {
                    // we create an instance implementing F on the fly
                    // so that high-level calling code stays clean
                    F function = (F) Proxy.newProxyInstance(functionClass.getClassLoader(),
                            new Class[] { functionClass },
                            (proxy, proxyMethod, args) -> method.invoke(instance, args));

                    map.put(annotation, function);
                });

        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T> T call(Object instance, Method method, Object... args) {
        try {
            return (T) method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException("Cannot call " + method.getName() + " Error: " + e);
        }
    }

    public static <T, R> Function<T, R> mapToFunction(Object instance, Method method) {
        return input -> call(instance, method, input);
    }

    public static <A extends java.lang.annotation.Annotation> Array<Field>
        findFieldsByAnnotation(Object instance, Class<A> annotationClass) {

        Array<Field> fields = new Array<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.getDeclaredAnnotation(annotationClass) != null) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Find all fields of instance that have type / subtype of given type parameter.
     *
     * @param instance object whose fields to search
     * @param type super type
     * @return all fields that meet criteria
     */
    public static Array<Field> findFieldsByType(Object instance, Class<?> type) {

        Array<Field> fields = new Array<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Injects field of an instance to injectionInstance.
     *
     * @param field the field object
     * @param instance field's object
     * @param injectionInstance the target value to inject
     */
    public static void inject(Field field, Object instance, Object injectionInstance) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(instance, injectionInstance);
        } catch (Exception e) {
            throw new RuntimeException("Cannot inject " + injectionInstance + " into " + field.getName() + " Error: " + e);
        }
    }
}
