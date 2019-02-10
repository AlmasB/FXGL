/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.reflect;

/**
 * Provides information about, and access to, an annotation of a field, class or interface.
 *
 * @author dludwig
 */
public final class Annotation {

    private java.lang.annotation.Annotation annotation;

    Annotation(java.lang.annotation.Annotation annotation) {
        this.annotation = annotation;
    }

    @SuppressWarnings("unchecked")
    public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<T> annotationType) {
        if (annotation.annotationType().equals(annotationType)) {
            return (T) annotation;
        }
        return null;
    }
}
