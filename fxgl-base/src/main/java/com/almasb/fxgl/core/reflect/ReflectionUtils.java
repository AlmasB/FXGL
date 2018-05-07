/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.reflect;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.util.Function;
import com.almasb.fxgl.util.Optional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.util.BackportKt.forEach;

/**
 * A collection of convenience methods to isolate reflection code.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ReflectionUtils {

    private ReflectionUtils() {}

    public static Method getMethod(Class<?> instanceClass, String methodName, Class<?>... paramTypes) {
        try {
            return instanceClass.getDeclaredMethod(methodName, paramTypes);
        } catch (Exception e) {
            throw new ReflectionException("Cannot get declared method <" + methodName + ">", e);
        }
    }

    public static <A extends java.lang.annotation.Annotation> Map<A, Method>
        findMethods(Object instance, Class<A> annotationClass) {

        Map<A, Method> map = new HashMap<>();

        for (Method method : instance.getClass().getDeclaredMethods()) {
            A annotation = method.getAnnotation(annotationClass);
            if (annotation != null) {
                map.put(annotation, method);
            }
        }

        return map;
    }

    public static <T, R, A extends java.lang.annotation.Annotation> Map<A, Function<T, R>>
        findMethodsMapToFunctions(Object instance, Class<A> annotationClass) {

        Map<A, Function<T, R>> map = new HashMap<>();

        forEach(
                findMethods(instance, annotationClass),
                (annotation, method) -> map.put(annotation, mapToFunction(instance, method))
        );

        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T, R, F extends Function<T, R>, A extends java.lang.annotation.Annotation> Map<A, F>
        findMethodsMapToFunctions(Object instance, Class<A> annotationClass, Class<F> functionClass) {

        Map<A, F> map = new HashMap<>();

        forEach(
                findMethods(instance, annotationClass),
                (annotation, method) -> {
                    // we create an instance implementing F on the fly
                    // so that high-level calling code stays clean
                    F function = (F) Proxy.newProxyInstance(functionClass.getClassLoader(),
                            new Class[] { functionClass },
                            (proxy, proxyMethod, args) -> method.invoke(instance, args));

                    map.put(annotation, function);
                }
        );

        return map;
    }

    public static <T> T callInaccessible(Object instance, Method method, Object... args) {
        method.setAccessible(true);

        return call(instance, method, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T call(Object instance, Method method, Object... args) {
        try {
            return (T) method.invoke(instance, args);
        } catch (Exception e) {
            throw new ReflectionException("Cannot call " + method.getName() + " Error: " + e);
        }
    }

    public static <T, R> Function<T, R> mapToFunction(Object instance, Method method) {
        return input -> call(instance, method, input);
    }

    public static <A extends java.lang.annotation.Annotation> Array<Field>
        findFieldsByAnnotation(Object instance, Class<A> annotationClass) {

        Array<Field> fields = new Array<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.getAnnotation(annotationClass) != null) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Find declared fields of "instance" that have type / subtype of given "type" parameter.
     *
     * @param instance object whose fields to search
     * @param type super type
     * @return declared fields that meet criteria
     */
    public static Array<Field> findDeclaredFieldsByType(Object instance, Class<?> type) {

        Array<Field> fields = new Array<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Find all fields of "instance" that have type / subtype of given "type" parameter.
     * Note: this will recursively search all matching fields in supertypes of "instance".
     *
     * @param instance object whose fields to search
     * @param type super type
     * @return all fields that meet criteria
     */
    public static Array<Field> findFieldsByTypeRecursive(Object instance, Class<?> type) {

        Array<Field> fields = new Array<>();

        for (Field field : getAllFieldsRecursive(instance)) {
            if (type.isAssignableFrom(field.getType())) {
                fields.add(field);
            }
        }

        return fields;
    }

    private static Array<Field> getAllFieldsRecursive(Object instance) {
        Array<Field> result = new Array<>();

        Class<?> typeClass = instance.getClass();
        while (typeClass != null && typeClass != Object.class) {
            result.addAll(typeClass.getDeclaredFields());
            typeClass = typeClass.getSuperclass();
        }

        return result;
    }

    public static Optional<Field> getDeclaredField(String fieldName, Object instance) {
        try {
            return Optional.of(instance.getClass().getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new ReflectionException("Cannot get declared field: " + fieldName + " of " + instance + " Error: " + e);
        }
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
            throw new ReflectionException("Cannot inject " + injectionInstance + " into " + field.getName() + " Error: " + e);
        }
    }

    /**
     * @param type class
     * @return instance of given class using its no-arg ctor
     * @throws ReflectionException if cannot be instantiated
     */
    public static <T> T newInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    public static boolean isAnonymousClass(Class<?> type) {
        return type.isAnonymousClass() || type.getCanonicalName() == null;
    }

    public static Throwable getRootCause(Throwable e) {
        Throwable cause;
        Throwable result = e;

        while (null != (cause = result.getCause())  && (result != cause) ) {
            result = cause;
        }
        return result;
    }
}
