/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.reflect;

import java.lang.reflect.InvocationTargetException;

/** Provides information about, and access to, a single constructor for a Class.
 * @author nexsoftware */
public final class Constructor {

    private final java.lang.reflect.Constructor constructor;

    Constructor(java.lang.reflect.Constructor constructor) {
        this.constructor = constructor;
    }

    /** Returns the Class object representing the class or interface that declares the constructor. */
    public Class getDeclaringClass() {
        return constructor.getDeclaringClass();
    }

    public void setAccessible(boolean accessible) {
        constructor.setAccessible(accessible);
    }

    /** Uses the constructor to create and initialize a new instance of the constructor's declaring class, with the supplied
     * initialization parameters. */
    public Object newInstance(Object... args) throws ReflectionException {
        try {
            return constructor.newInstance(args);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException("Illegal argument(s) supplied to constructor for class: " + getDeclaringClass().getName(),
                    e);
        } catch (InstantiationException e) {
            throw new ReflectionException("Could not instantiate instance of class: " + getDeclaringClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Could not instantiate instance of class: " + getDeclaringClass().getName(), e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException("Exception occurred in constructor for class: " + getDeclaringClass().getName(), e);
        }
    }

}
