package com.almasb.fxgl.entity;

public class Pair<T> {

    private T a, b;

    public Pair(T a, T b) {
        if (a == null || b == null)
            throw new IllegalArgumentException("Entities must not be null: "
                    + a == null ? "a" : "b");
        this.a = a;
        this.b = b;
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof Pair))
            return false;

        Pair<?> pair = (Pair<?>) o;
        return (pair.a == a && pair.b == b)
                || (pair.a == b && pair.b == a);
    }

    @Override
    public int hashCode() {
        return a.hashCode() + b.hashCode();
    }
}
