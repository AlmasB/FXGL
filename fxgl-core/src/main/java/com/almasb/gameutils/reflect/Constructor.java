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

package com.almasb.gameutils.reflect;

import java.lang.reflect.InvocationTargetException;

/** Provides information about, and access to, a single constructor for a Class.
 * @author nexsoftware */
public final class Constructor {

    private final java.lang.reflect.Constructor constructor;

    Constructor(java.lang.reflect.Constructor constructor) {
        this.constructor = constructor;
    }

    /** Returns an array of Class objects that represent the formal parameter types, in declaration order, of the constructor. */
    public Class[] getParameterTypes() {
        return constructor.getParameterTypes();
    }

    /** Returns the Class object representing the class or interface that declares the constructor. */
    public Class getDeclaringClass() {
        return constructor.getDeclaringClass();
    }

    public boolean isAccessible() {
        return constructor.isAccessible();
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
