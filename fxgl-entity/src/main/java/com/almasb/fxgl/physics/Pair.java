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
    public boolean equals(Object o) {
        return equal((Pair<?>) o, a, b);
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
        return equal(this, a, b);
    }

    @SuppressWarnings("PMD.UselessParentheses")
    private static boolean equal(Pair<?> pair, Object a, Object b) {
        return (pair.a == a && pair.b == b) || (pair.a == b && pair.b == a);
    }
}
