/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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
package com.almasb.fxgl.physics;

/**
 * For internal use.
 *
 * @param <T> pair type
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Pair<T> {

    private T a, b;

    Pair(T a, T b) {
        this.a = a;
        this.b = b;
    }

    T getA() {
        return a;
    }

    T getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        Pair<?> pair = (Pair<?>) o;
        return (pair.a == a && pair.b == b)
                || (pair.a == b && pair.b == a);
    }

    @Override
    public int hashCode() {
        int hash = 0;

        if (a != null)
            hash += a.hashCode();

        if (b != null)
            hash += b.hashCode();

        return hash;
    }

    /**
     * Note: order doesn't matter.
     *
     * @param a pair element
     * @param b pair element
     * @return true iff this pair equals given pair elements
     */
    public boolean equal(T a, T b) {
        return (this.a == a && this.b == b)
                || (this.a == b && this.b == a);
    }
}
