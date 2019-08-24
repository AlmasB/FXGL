/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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

    void setA(T a) {
        this.a = a;
    }

    T getB() {
        return b;
    }

    void setB(T b) {
        this.b = b;
    }

    @Override
    @SuppressWarnings("PMD.UselessParentheses")
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
    @SuppressWarnings("PMD.UselessParentheses")
    public boolean equal(T a, T b) {
        return (this.a == a && this.b == b)
                || (this.a == b && this.b == a);
    }
}
