/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.reflect;

import java.lang.reflect.Modifier;

/** Provides information about, and access to, a single field of a class or interface.
 * @author nexsoftware */
public final class Field {

    private final java.lang.reflect.Field field;

    Field(java.lang.reflect.Field field) {
        this.field = field;
    }

    /** Returns the name of the field. */
    public String getName() {
        return field.getName();
    }

    /** Returns a Class object that identifies the declared type for the field. */
    public Class getType() {
        return field.getType();
    }

    /** Returns the Class object representing the class or interface that declares the field. */
    public Class getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public void setAccessible(boolean accessible) {
        field.setAccessible(accessible);
    }

    /** Return true if the field includes the {@code final} modifier. */
    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    /** Return true if the field includes the {@code private} modifier. */
    public boolean isPrivate() {
        return Modifier.isPrivate(field.getModifiers());
    }

    /** Return true if the field includes the {@code protected} modifier. */
    public boolean isProtected() {
        return Modifier.isProtected(field.getModifiers());
    }

    /** Return true if the field includes the {@code public} modifier. */
    public boolean isPublic() {
        return Modifier.isPublic(field.getModifiers());
    }

    /** Return true if the field includes the {@code static} modifier. */
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    /** Returns an {@link Annotation} object reflecting the annotation provided, or null of this field doesn't
     * have such an annotation. This is a convenience function if the caller knows already which annotation
     * type he's looking for. */
    public Annotation getDeclaredAnnotation(Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
        if (annotations == null) {
            return null;
        }
        for (java.lang.annotation.Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationType)) {
                return new Annotation(annotation);
            }
        }
        return null;
    }

    /** Returns the value of the field on the supplied object. */
    public Object get(Object obj) throws ReflectionException {
        try {
            return field.get(obj);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException("Object is not an instance of " + getDeclaringClass(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Illegal access to field: " + getName(), e);
        }
    }

    /** Sets the value of the field on the supplied object. */
    public void set(Object obj, Object value) throws ReflectionException {
        try {
            field.set(obj, value);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException("Argument not valid for field: " + getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Illegal access to field: " + getName(), e);
        }
    }

}
