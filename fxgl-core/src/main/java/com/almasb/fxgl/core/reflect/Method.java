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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/** Provides information about, and access to, a single method on a class or interface.
 * @author nexsoftware */
public final class Method {

    private final java.lang.reflect.Method method;

    Method(java.lang.reflect.Method method) {
        this.method = method;
    }

    /** Returns the name of the method. */
    public String getName() {
        return method.getName();
    }

    /** Returns a Class object that represents the formal return type of the method. */
    public Class getReturnType() {
        return method.getReturnType();
    }

    /** Returns an array of Class objects that represent the formal parameter types, in declaration order, of the method. */
    public Class[] getParameterTypes() {
        return method.getParameterTypes();
    }

    /** Returns the Class object representing the class or interface that declares the method. */
    public Class getDeclaringClass() {
        return method.getDeclaringClass();
    }

    public boolean isAccessible() {
        return method.isAccessible();
    }

    public void setAccessible(boolean accessible) {
        method.setAccessible(accessible);
    }

    /** Return true if the method includes the {@code abstract} modifier. */
    public boolean isAbstract() {
        return Modifier.isAbstract(method.getModifiers());
    }

    /** Return true if the method does not include any of the {@code private}, {@code protected}, or {@code public} modifiers. */
    public boolean isDefaultAccess() {
        return !isPrivate() && !isProtected() && !isPublic();
    }

    /** Return true if the method includes the {@code final} modifier. */
    public boolean isFinal() {
        return Modifier.isFinal(method.getModifiers());
    }

    /** Return true if the method includes the {@code private} modifier. */
    public boolean isPrivate() {
        return Modifier.isPrivate(method.getModifiers());
    }

    /** Return true if the method includes the {@code protected} modifier. */
    public boolean isProtected() {
        return Modifier.isProtected(method.getModifiers());
    }

    /** Return true if the method includes the {@code public} modifier. */
    public boolean isPublic() {
        return Modifier.isPublic(method.getModifiers());
    }

    /** Return true if the method includes the {@code native} modifier. */
    public boolean isNative() {
        return Modifier.isNative(method.getModifiers());
    }

    /** Return true if the method includes the {@code static} modifier. */
    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    /** Return true if the method takes a variable number of arguments. */
    public boolean isVarArgs() {
        return method.isVarArgs();
    }

    /** Invokes the underlying method on the supplied object with the supplied parameters. */
    public Object invoke(Object obj, Object... args) throws ReflectionException {
        try {
            return method.invoke(obj, args);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException("Illegal argument(s) supplied to method: " + getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Illegal access to method: " + getName(), e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException("Exception occurred in method: " + getName(), e);
        }
    }

    /** Returns true if the method includes an annotation of the provided class type. */
    public boolean isAnnotationPresent(Class<? extends java.lang.annotation.Annotation> annotationType) {
        return method.isAnnotationPresent(annotationType);
    }

    /** Returns an array of {@link Annotation} objects reflecting all annotations declared by this method,
     * or an empty array if there are none. Does not include inherited annotations.
     * Does not include parameter annotations. */
    public Annotation[] getDeclaredAnnotations() {
        java.lang.annotation.Annotation[] annotations = method.getDeclaredAnnotations();
        Annotation[] result = new Annotation[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            result[i] = new Annotation(annotations[i]);
        }
        return result;
    }

    /** Returns an {@link Annotation} object reflecting the annotation provided, or null of this method doesn't
     * have such an annotation. This is a convenience function if the caller knows already which annotation
     * type he's looking for. */
    public Annotation getDeclaredAnnotation(Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation[] annotations = method.getDeclaredAnnotations();
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

}
